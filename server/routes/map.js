const express = require('express');
const router = express.Router();

const Map = require('../bbdd/MapSchema');
const Point = require('../bbdd/PointSchema');

router.get('/', (req, res) => { //get all maps summary info
    res.send("This is the map route");
});

router.get('/get', (req, res) =>{ // get specific map summary info

    res.send("This is the map/get route");
});


router.post('/point', async (req, res) =>{

    try {
        const point = new Point({
            name:           req.body.name,
            owner:          req.body.owner,
            type:           req.body.type,
            description:    req.body.description,
            public:         req.body.public,
            coord: {
                lat:  req.body.coord.lat,
                long: req.body.coord.long,
            }
        });
        const savedUser = await point.save();
        res.json(savedUser);
    } catch(err) {
        console.error(err);
        res.json({ "error": err.message});
    }
});


router.post('/add', async(req, res) => { 

    try {
        const map = new Map({
            name:           req.body.name,
            owner:          req.body.owner,
            metadata: {
                lat:  req.body.metadata.author,
                long: req.body.metadata.description,
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

;

module.exports = router;