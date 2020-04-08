const express = require('express');
const router = express.Router();
const Map = require('../bbdd/MapSchema');

router.get('/', (req, res) => { //get all maps summary info

    try {
        if (req.body._id) { // ID received: send one map.
            Map.findById(req.body._id).populate('points')
                .exec(function (err, map) {
                    if (err) res.status(400).json({ "error": err.message });
                    else if (map == null)
                        res.status(404).json({ "error": 'Map does not exist' });
                    else res.status(200).json({
                        "_id": map._id,
                        "name": map.name,
                        "metadata": map.metadata,
                        "start": map.points[0].coord
                    });
                })
        }
        else { // Send all maps
            Map.find({}, { // Seleccionar tots els mapes.
                "name": 1, // Retornar certs camps dels mapes seleccionats.
                "metadata.description": 1
            },
                function (err, maps) {
                    if (err) res.status(400).json({ "error": err.message });
                    else res.status(200).json(maps);
                }
            )
        }
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        const map = new Map({
            name: req.body.name,
            owner: req.body.owner,
            metadata: {
                author: req.body.metadata.author,
                description: req.body.metadata.description,
            },
            points: req.body.points
        });
        await map.save(function (err, savedMap) {
            if (err) {
                if (err.message.substring(0, 6) == 'E11000')
                    res.status(409).json({ "error": "Map already exists." });
                else res.status(400).json({ "error": err.message });
            }
            else res.status(200).json(savedMap);
        });
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

module.exports = router;