const express = require('express');
const router = express.Router();
const Map = require('../bbdd/MapSchema');

router.get('/', (req, res) => { //get all maps summary info
    
    try{
        if (req.body._id){ // ID received: send one map
            console.log("I received an ID");
        }
        else{ // Send all maps
            Map.find({"owner": "guillem"},{ // mirar més acuradament per retornar tots els mapes
                "_id": 1,         // mirar més acuradament per retornar certs camps
                "name": 1},
                function (err, maps) {
                if (err) return console.error(err);
                res.json(maps);
            })
        }
    } catch(err) {
        res.json({ "error": err.message});
    }
});

router.post('/', async(req, res) => { 

    try {
        const map = new Map({
            name:           req.body.name,
            owner:          req.body.owner,
            metadata: {
                author:  req.body.metadata.author,
                description: req.body.metadata.description,
            },
            points: req.body.points
        });
        const savedUser = await map.save();
        res.json(savedUser);
    } catch(err) {
        console.error(err);
        res.json({ "error": err.message});
    }
});

module.exports = router;