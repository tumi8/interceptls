var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database('db.sqlite');
var uuid = require('uuid');

db.run('CREATE TABLE IF NOT EXISTS session (id text PRIMARY KEY)');
db.run('CREATE TABLE IF NOT EXISTS handshake (id INTEGER PRIMARY KEY, session_id text, destination text, receivedBytes text, sentBytes text, FOREIGN KEY(session_id) REFERENCES session(id))');

var createNewSession = function (callback){
  var id = uuid.v4();
  db.run('INSERT INTO session (id) VALUES (?)', id, function(err, rows){
    if(err){
      console.error(err);
      err = new Error('Could not insert new session id.');
    }
    callback(err, id);
  });
}

var uploadHandshake = function(id, destination, receivedBytes, sentBytes, callback){
  db.run('INSERT INTO handshake VALUES (null, ?, ?, ?, ?)', id, destination, receivedBytes, sentBytes, function(err, rows){
    if(err){
      console.error(err);
      err = new Error('Could not insert handshake.');
    }
    callback(err);
  });
}

module.exports = {
  createNewSession: createNewSession,
  uploadHandshake: uploadHandshake,
}
