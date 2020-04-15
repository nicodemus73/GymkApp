const express = require('express');
const router = express.Router();
const Point = require('../bbdd/PointSchema');

router.get('/', (req, res) => { //get all maps summary info

    try {
        if (req.body._id) { // ID received: send one point.
            Point.findById(req.body._id, { "name": 1, "type": 1, "description":1, "location": 1 }).exec(function (err, point) {
                if (err) res.status(400).json({ "error": err.message });
                else if (point == null)
                    res.status(404).json({ "error": 'Point does not exist' });
                else res.status(200).json(point);
            })
        }
        else if (req.body.location && req.body.radius) { // Send all nearby points.
            Point.find({
                location: {
                    $near: {
                        $maxDistance: req.body.radius, // distance in meters
                        $geometry: req.body.location
                    }
                }
            }, { "name": 1, "type": 1, "description":1, "location": 1 }).find((err, points) => {
                if (err) res.status(400).json({ "error": err.message });
                else res.status(200).json(points);
            });
        }
        else res.status(400).json({ "error": 'Invalid parameters' })
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        const point = new Point({
            name: req.body.name,
            owner: req.usernameId._id,
            type: req.body.type,
            description: req.body.description,
            public: req.body.public,
            location: req.body.location
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