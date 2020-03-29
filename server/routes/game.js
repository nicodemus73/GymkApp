const express = require('express');
const router = express.Router();

const jwt = require('jsonwebtoken');
const verifyToken = require('../functMiddle/VerifyToken');
const Usuario = require('../bbdd/Usuario');

router.get('/', (req, res) => {
    //console.log(Date.now.toString);
});

//Add game to the user falta mirar si ja existex etc etc

router.post('/add_game', verifyToken, async (req, res) => {
    try {
    
        const myquery  = {_id: req.usernameId._id};
        const newvalues = {'games': {_id: req.body._id, name: req.body.name, owner: req.body.owner, metadata: { author: req.body.metadata.author, description: req.body.metadata.description}, points: req.body.points}};
        const resultat = await Usuario.updateOne(req.usernameId_id, {$addToSet: newvalues}, function(err, res) {
            if (err) throw err;
            console.log("1 document updated");
            
        });

        res.json(resultat);
    }  catch (error) {
        res.json({ "error": error.message });
    }
});

module.exports = router;