var express = require('express');
var router = express.Router();

/* PUT a new captured handshake. */
router.put('/:session', function(req, res, next) {
  var session = req.params.session;
  //TODO input validation
  console.log("Receive captured handshake for session id [" + session + "]")

  res.send("OK");
});

module.exports = router;
