var supertest = require('supertest'),
assert = require('assert'),
app = require('../../app');
var uuid = require("uuid");

var id;

exports.create_new_session_id = function(done){
  supertest(app)
  .post('/session')
  .expect(200)
  .end(function(err, response){
    if(err){
      console.log(err);
    }
    assert.ok(!err);
    id = response.body.id;
    var result = id.match(/[A-Za-z0-9]+/);
    assert.ok(result != null);
    return done();
  });
};

exports.add_captured_handshake = function(done){
  supertest(app)
  .put('/handshake/' + id)
  .send({
    destination : 'www.heise.de',
    receivedBytes: 'dGVzdAo=',
    sentBytes: 'dGVzdAo=',
  })
  .expect(200)
  .end(done);
};
