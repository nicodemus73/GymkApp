const express = require('express');
const router = express.Router();
const Point = require('../bbdd/PointSchema');
const postPoint = require('../auxiliary/postPoint');

router.get('/', (req, res) => { //get all maps summary info

    try {
        if (req.body.location && req.body.radius) { // Send all nearby points.
            Point.find({
                location: {
                    $near: {
                        $maxDistance: req.body.radius, // distance in meters
                        $geometry: req.body.location
                    }
                }
            }, { "name": 1, "type": 1, "description": 1, "location": 1 }).find((err, points) => {
                if (err) res.status(400).json({ "error": err.message });
                else res.status(200).json(points);
            });
        }
        else res.status(400).json({ "error": 'Invalid parameters' })
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.get('/:id', (req, res) => { //get all maps summary info

    try {
        Point.findById(req.params.id, { "name": 1, "type": 1, "description": 1, "location": 1 }).exec(function (err, point) {
            if (err) res.status(400).json({ "error": err.message });
            else if (point == null)
                res.status(404).json({ "error": 'Point does not exist' });
            else res.status(200).json(point);
        })
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        const { code, result } = await postPoint(req, req.body);
        res.status(code).json(result);

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

module.exports = router;
