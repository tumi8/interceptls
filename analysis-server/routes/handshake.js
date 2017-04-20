var express = require('express');
var router = express.Router();
var db = require('../db/db');

/* PUT a new captured handshake. */
router.put('/:session', function(req, res, next) {

  req.checkParams('session', 'Invalid session id').notEmpty().isUUID();
  req.checkBody('destination', 'Invalid destination').notEmpty();
  req.checkBody('receivedBytes', 'Invalid receivedBytes').notEmpty();
  req.checkBody('sentBytes', 'Invalid sentBytes').notEmpty();

  var err = req.validationErrors();
  if (err) {
    err.status = 400;
    next(err);
  }
  else{
    var session = req.params.session;
    console.log("Receive captured handshake for id [" + session + "]");

    db.uploadHandshake(session, req.body.destination, req.body.receivedBytes, req.body.sentBytes, function(err){
      if(err) {
        err.status = 400;
        next(err);
      }else{
        res.send("OK");
      }
    })
  }




});

module.exports = router;
