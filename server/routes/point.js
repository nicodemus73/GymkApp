const express = require('express');
const router = express.Router();
const Point = require('../bbdd/PointSchema');

router.post('/', async (req, res) =>{

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

        await point.save(function (err, savedPoint) {
            if (err) return console.error(err);
            res.json(savedPoint);
        });
    } catch(err) {
        res.json({ "error": err.message});
    }
});

module.exports = router;