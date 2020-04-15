const express = require('express');
const router = express.Router();
const Map = require('../bbdd/MapSchema');
const Point = require('../bbdd/PointSchema');

router.get('/', (req, res) => { //get all maps summary info

    try {
        if (req.body._id) { // ID received: send one map.
            Map.findById(req.body._id, { "name": 1, "metadata": 1, "firstLocation": 1 }).exec(function (err, map) {
                if (err) res.status(400).json({ "error": err.message });
                else if (map == null)
                    res.status(404).json({ "error": 'Map does not exist' });
                else res.status(200).json(map);
            })
        }
        else if (req.body.location && req.body.radius) { // Send all nearby maps.
            Map.find({
                firstLocation: {
                    $near: {
                        $maxDistance: req.body.radius, // distance in meters
                        $geometry: req.body.location
                    }
                }
            }, { "name": 1, "metadata": 1, "firstLocation": 1 }).find((err, maps) => {
                if (err) res.status(400).json({ "error": err.message });
                else res.status(200).json(maps);
            });
        }
        else res.status(400).json({ "error": 'Invalid parameters' })
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        if (req.body.points.length < 2) throw new Error('400Map needs at least two points');

        const point = Point.findById(req.body.points[0], async function (err, point) {
            if (err) res.status(400).json({ "error": err.message });
            else if (point == null)
                res.status(404).json({ "error": 'First point is not valid.' });
            else {

                const map = new Map({
                    name: req.body.name,
                    owner: req.usernameId._id,
                    metadata: {
                        author: req.body.metadata.author,
                        description: req.body.metadata.description,
                    },
                    points: req.body.points,
                    firstLocation: point.location
                });
                await map.save(function (err, savedMap) {
                    if (err) {
                        if (err.message.substring(0, 6) == 'E11000')
                            res.status(409).json({ "error": "Map already exists." });
                        else res.status(400).json({ "error": err.message });
                    }
                    else res.status(200).json(savedMap);
                });
            }
        });
    } catch (err) {
        if (!isNaN(err.message.substring(0, 3))) // Check if it is a custom thrown error.
            res.status(err.message.substring(0, 3)).json({ "error": err.message.substring(3, 1e10000) }); // 1e10000 = infinite
        else res.status(500).json({ "error": err.message });
    }
});

module.exports = router;

