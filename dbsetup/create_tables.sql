-- Create Table

CREATE TABLE queue (
  id SERIAL PRIMARY KEY,
  time TIMESTAMP DEFAULT NOW(),
  creator INTEGER NOT NULL
 );
 
 CREATE TABLE message (
  id SERIAL PRIMARY KEY,
  time TIMESTAMP DEFAULT NOW(),
  sender INTEGER NOT NULL,
  receiver INTEGER,
  qid INTEGER NOT NULL,
  message TEXT NOT NULL,
  CONSTRAINT qid FOREIGN KEY (qid)
    REFERENCES queue (id)
);


-- Create Function

CREATE OR REPLACE FUNCTION create_queue(_creator INT) RETURNS TABLE(id INT) AS $$
    INSERT INTO queue(creator) VALUES(_creator) RETURNING id;
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION delete_queue(_id INT) RETURNS void AS $$
    DELETE FROM queue WHERE id = _id;
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION send_message_broadcast(_sender INT, _qid INT, _message TEXT) RETURNS void AS $$
    INSERT INTO message(sender, receiver, qid, message) VALUES(_sender, null, _qid, _message);
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION send_message_to_receiver(_sender INT, _receiver INT, _qid INT, _message TEXT) RETURNS void AS $$
    INSERT INTO message(sender, receiver, qid, message) VALUES(_sender, _receiver, _qid, _message);
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION delete_message_from_queue(_qid INT, _receiver INT) RETURNS TABLE(id INT, sender INT, message TEXT) AS $$
    DELETE FROM message AS m1 WHERE m1.id = (
        SELECT m2.id FROM message AS m2 WHERE (receiver = _receiver OR receiver IS NULL) AND qid = _qid
        ORDER BY qid, time LIMIT 1 FOR UPDATE
    ) RETURNING m1.id, m1.sender, m1.message;
$$ LANGUAGE sql VOLATILE;

CREATE OR REPLACE FUNCTION peek_message_from_queue(_qid INT, _receiver INT) RETURNS TABLE(id INT, sender INT, message TEXT) AS $$
    SELECT id, sender, message FROM message WHERE (receiver = _receiver OR receiver IS NULL) AND qid = _qid 
    ORDER BY qid, time LIMIT 1;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION peek_message_from_sender(_sender INT, _receiver INT) RETURNS TABLE(id INT, sender INT, message TEXT) AS $$
    SELECT id, sender, message FROM message WHERE (receiver = _receiver OR receiver IS NULL) AND sender = _sender
    ORDER BY sender, time LIMIT 1;
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION query_queues_for_receiver(_receiver INT) RETURNS TABLE(id INT) AS $$
    SELECT DISTINCT ON (qid) qid FROM message WHERE receiver = _receiver ORDER BY qid;
$$ LANGUAGE sql IMMUTABLE;


-- Create Index

CREATE INDEX idx_receiver_qid ON message(receiver, qid);
CREATE INDEX idx_qid_time ON message(qid, time);
CREATE INDEX idx_sender_time ON message(sender, time);


