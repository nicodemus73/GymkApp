const express = require('express');
const router = express.Router();
const Point = require('../bbdd/PointSchema');

router.post('/', async (req, res) => {

    try {
        const point = new Point({
            name: req.body.name,
            owner: req.usernameId._id,
            type: req.body.type,
            description: req.body.description,
            public: req.body.public,
            coord: {
                lat: req.body.coord.lat,
                long: req.body.coord.long,
            }
        });

        await point.save(function (err, savedPoint) {
            if (err) {
                if (err.message.substring(0, 6) == 'E11000')
                    res.status(409).json({ "error": "Point already exists." });
                else res.status(400).json({ "error": err.message });
            }
            else res.status(200).json(savedPoint);
        });
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

module.exports = router;