const express = require('express');
const router = express.Router();
const Map = require('../bbdd/MapSchema');
const Point = require('../bbdd/PointSchema');
const utils = require('../auxiliary/utils');
const postPoint = require('../auxiliary/postPoint');

router.get('/', (req, res) => { 

    try {
        if (Number(req.query.lon) && Number(req.query.lat) && Number(req.query.radius)) { // Send all nearby maps.
            const location = { "type": "Point", "coordinates": [Number(req.query.lon), Number(req.query.lat)] }
            Map.find({
                firstLocation: {
                    $near: {
                        $maxDistance: Number(req.query.radius), // distance in meters
                        $geometry: location
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

router.get('/my', (req, res) => {

    try {
        Map.find({ owner: req.usernameId._id }).exec(function (err, map) {
            if (err) res.status(400).json({ "error": err.message });
            else if (map == null)
                res.status(404).json({ "error": 'Map does not exist' });
            else res.status(200).json(map);
        })

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.get('/my/:id', (req, res) => {

    try {
        Map.find({ _id: req.params.id, owner: req.usernameId._id }).exec(function (err, map) {
            if (err) res.status(400).json({ "error": err.message });
            else if (map == null)
                res.status(404).json({ "error": 'Map does not exist' });
            else res.status(200).json(map);
        })

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.get('/:id', (req, res) => {

    try {
        Map.findById(req.params.id, { "name": 1, "metadata": 1, "firstLocation": 1 }).exec(function (err, map) {
            if (err) res.status(400).json({ "error": err.message });
            else if (map == null)
                res.status(404).json({ "error": 'Map does not exist' });
            else res.status(200).json(map);
        })

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        var pointsId = [];
        if (req.body.points.length < 2) throw new Error('400Map needs at least two points');

        for (var i = 0; i < req.body.points.length; i++) {
            if (!utils.validateId(req.body.points[i]._id)) {
                const { code, result } = await postPoint(req, req.body.points[i]);
                if (code != 200) throw new Error(code + result.error);
                req.body.points[i]._id = result._id;
            }
            pointsId.push(req.body.points[i]._id);
        }

        Point.findById(req.body.points[0]._id, async function (err, point) {
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
                    points: pointsId,
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

