var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database('db.sqlite');
var uuid = require("uuid");

db.run('CREATE TABLE IF NOT EXISTS session (id text UNIQUE)');

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

module.exports = {
  createNewSession: createNewSession,
}
