var express = require('express');
var router = express.Router();
var db = require('../db/db');

/* POST a new session id. */
router.post('/', function(req, res, next) {
  var id = db.createNewSession(function(err, id){
    if(err) {
      err.status = 400;
      next(err);
    }else{
      res.send({
        id: id
      });
    }

  });

});

module.exports = router;
