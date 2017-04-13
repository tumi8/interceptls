var express = require('express');
var router = express.Router();

/* POST get a new session id. */
router.post('/', function(req, res, next) {
  var id = {
    id : '12'
    //optional access token
  }
  res.send(id);
});

module.exports = router;
