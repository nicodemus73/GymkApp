const express = require('express');
const router = express.Router();
const Map = require('../bbdd/MapSchema');

router.get('/', (req, res) => { //get all maps summary info

    try {
        if (req.body._id) { // ID received: send one map.
            Map.findById(req.body._id, {
                "name": 1,
                "metadata": 1,
                "points": 1
            }).populate('points')
                .exec(function (err, map) {
                    if (err) return console.error(err);
                    res.json({
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
                    if (err) return console.error(err);
                    res.json(maps);
                }
            )
        }
    } catch (err) {
        res.json({ "error": err.message });
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
        const savedUser = await map.save();
        res.json(savedUser);
    } catch (err) {
        console.error(err);
        res.json({ "error": err.message });
    }
});

module.exports = router;