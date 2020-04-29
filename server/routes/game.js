const express = require('express');
const router = express.Router();
const Game = require('../bbdd/GameSchema');
const Map = require('../bbdd/MapSchema');
const Point = require('../bbdd/PointSchema');
const User = require('../bbdd/UserSchema');
const jwt = require('jsonwebtoken');
const distance = require('turf-distance');
const utils = require('../auxiliary/utils');

router.get('/my', async (req, res) => {

    try {
        Game.find({ user: req.usernameId._id }, function (err, games) {
            if (err) res.status(400).json({ "error": err.message });
            else if (games.length == 0)
                res.status(404).json({ "error": 'User has played no games.' });
            else res.status(200).json(games);
        });

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.get('/map/:id', async (req, res) => {

    try {
        Game.find({ map: req.params.id }, function (err, games) {
            if (err) res.status(400).json({ "error": err.message });
            else if (games.length == 0)
                res.status(404).json({ "error": 'No games played on this map.' });
            else res.status(200).json(games);
        });

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.get('/:id', async (req, res) => {

    try {
        Game.findById(req.params.id, function (err, game) {
            if (err) res.status(400).json({ "error": err.message });
            else if (game == null)
                res.status(404).json({ "error": 'Game does not exist.' });
            else res.status(200).json(game);
        });

    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
});

router.post('/demo/new', async (req, res) => {

    try {
        //if (!req.body.location) throw new Error('400No location sent');
        //const map = await Map.findById(req.params.id); // so we can dereference point 0

        Map.findOne({}, {}, { sort: { 'date': -1 } }, async function (err, map) {
            if (err) res.status(400).json({ "error": err.message, "message": null, "location": null, "time": null });
            else {
                //if (map == null) throw new Error('404Map does not exist'); // protect against nonexistant maps
                /*if (distance(req.body.location, map.firstLocation) * 1000 > process.env.ONFOOT_START_DIST)
                    throw new Error('400You are too far from start point');*/

                const game = new Game({
                    user: req.usernameId._id,
                    map: map._id,
                    startLocation: map.firstLocation,
                    progress: {
                        point: map.points[0], // point 0 dereferenced
                    }
                });

                await game.save(async function (err, savedGame) {
                    if (err) res.status(400).json({ "error": err.message, "message": null, "location": null, "time": null });
                    else {
                        const point = await Point.findById(map.points[0]); // we suppose points are well maintained
                        res.status(200).json({
                            "message": point.description,
                            "location": point.location,
                            "time": null,
                            "error": null
                        });
                    }
                });
            }
        });
    } catch (err) {
        if (!isNaN(err.message.substring(0, 3))) // Check if it is a custom thrown error.
            res.status(err.message.substring(0, 3)).json({ "error": err.message.substring(3, 1e10000), "message": null, "location": null, "time": null }); // 1e10000 = infinite
        else res.status(500).json({ "error": err.message, "message": null, "location": null, "time": null });
    }
});

router.post('/demo', async (req, res) => {

    try {
        if (!req.body.location) throw new Error('400No location sent');
        //const game = await Game.findById(req.params.id);

        Game.findOne({}, {}, { sort: { 'startDate': -1 } }, async function (err, game) {
            if (err) res.status(400).json({ "error": err.message, "message": null, "location": null, "time": null });
            else if (game.status != 'inProgress') res.status(400).json({ "error": 'Game is not in progress', "message": null, "location": null, "time": null });
            else {

                //if (game == null) throw new Error('404Game does not exist');
                //if (game.user != req.usernameId._id) throw new Error('400This game is not yours');
                //if (game.status != 'inProgress') throw new Error('400Game is not in progress');

                const currentPoint = await Point.findById(game.progress[game.progress.length - 1].point);

                if (distance(req.body.location, currentPoint.location) * 1000 < process.env.ONFOOT_CHECK_DIST) {
                    // point completed

                    const date = new Date();
                    game.progress[game.progress.length - 1].completedDate = date;
                    const map = await Map.findById(game.map); // we suppose maps and points are well maintained

                    if (map.points.length == game.progress.length) { // this is the last point validation
                        console.log('Last Point');
                        game.status = 'completed';
                        game.endDate = date;
                        res.status(200).json({
                            "message": game.status,
                            "location": null,
                            "time": utils.parseTime(new Date(game.endDate - game.startDate)),
                            "error": null
                        })
                    }
                    else { // this is not the last point validation: send next.
                        console.log('Not Last Point');
                        const point = await Point.findById(map.points[game.progress.length]);
                        game.progress.push({ point: point._id });
                        res.status(200).json({
                            "message": point.description,
                            "location": point.location,
                            "time": null,
                            "error": null
                        });
                    }
                }
                else {
                    res.status(400).json({ "error": "Point is not completed yet", "message": null, "location": null, "time": null });
                    game.progress[game.progress.length - 1].tries++;
                }
                await game.save();
            }
        });
    } catch (err) {
        if (!isNaN(err.message.substring(0, 3))) // Check if it is a custom thrown error.
            res.status(err.message.substring(0, 3)).json({ "error": err.message.substring(3, 1e10000), "message": null, "location": null, "time": null }); // 1e10000 = infinite
        else res.status(500).json({ "error": err.message, "message": null, "location": null, "time": null });
    }
});

router.post('/map/:id', async (req, res) => {

    try {
        if (!req.body.location) throw new Error('400No location sent');

        const map = await Map.findById(req.params.id); // so we can dereference point 0
        if (map == null) throw new Error('404Map does not exist'); // protect against nonexistant maps
        if (distance(req.body.location, map.firstLocation) * 1000 > process.env.ONFOOT_START_DIST)
            throw new Error('400You are too far from start point');
        const game = new Game({
            user: req.usernameId._id,
            map: req.params.id,
            startLocation: req.body.location,
            progress: {
                point: map.points[0], // point 0 dereferenced
            }
        });
        await game.save(async function (err, savedGame) {
            if (err) res.status(400).json({ "error": err.message });
            else {
                const point = await Point.findById(map.points[0]); // we suppose points are well maintained
                res.status(200).json({
                    "_id": savedGame._id,
                    "description": point.description,
                    "location": point.location
                });
            }
        });

    } catch (err) {
        if (!isNaN(err.message.substring(0, 3))) // Check if it is a custom thrown error.
            res.status(err.message.substring(0, 3)).json({ "error": err.message.substring(3, 1e10000) }); // 1e10000 = infinite
        else res.status(500).json({ "error": err.message });
    }
});

router.post('/:id', async (req, res) => {

    try {
        if (!req.body.location) throw new Error('400No location sent');
        const game = await Game.findById(req.params.id);
        if (game == null) throw new Error('404Game does not exist');
        if (game.user != req.usernameId._id) throw new Error('400This game is not yours');
        if (game.status != 'inProgress') throw new Error('400Game is not in progress');

        const currentPoint = await Point.findById(game.progress[game.progress.length - 1].point);

        if (distance(req.body.location, currentPoint.location) * 1000 < process.env.ONFOOT_CHECK_DIST) {
            // point completed

            const date = new Date();
            game.progress[game.progress.length - 1].completedDate = date;
            const map = await Map.findById(game.map); // we suppose maps and points are well maintained

            if (map.points.length == game.progress.length) { // this is the last point validation
                console.log('Last Point');
                game.status = 'completed';
                game.endDate = date;
                res.status(200).json({
                    "status": game.status,
                    "time": utils.parseTime(new Date(game.endDate - game.startDate))
                })
            }
            else { // this is not the last point validation: send next.
                console.log('Not Last Point');
                const point = await Point.findById(map.points[game.progress.length]);
                game.progress.push({ point: point._id });
                res.status(200).json({
                    "description": point.description,
                    "location": point.location
                });
            }
        }
        else {
            res.status(400).json({ "error": "Point is not completed yet" });
            game.progress[game.progress.length - 1].tries++;
        }
        await game.save();

    } catch (err) {
        if (!isNaN(err.message.substring(0, 3))) // Check if it is a custom thrown error.
            res.status(err.message.substring(0, 3)).json({ "error": err.message.substring(3, 1e10000) }); // 1e10000 = infinite
        else res.status(500).json({ "error": err.message });
    }
});

//Add game to the user falta mirar si ja existex etc etc
router.post('/add_game', async (req, res) => {
    try {

        const myquery = { _id: req.usernameId._id };
        const newvalues = {
            'games': {
                _id: req.body._id,
                name: req.body.name,
                owner: req.body.owner,
                metadata: {
                    author: req.body.metadata.author,
                    description: req.body.metadata.description
                },
                points: req.body.points
            }
        };
        const resultat = await User.updateOne(req.usernameId_id, { $addToSet: newvalues }, function (err, res) {
            if (err) res.json(err.message);
            //console.log("1 document updated");

        });
        res.json(resultat); //cambiar
    } catch (error) {
        res.json({ "error": error.message });
    }
});

module.exports = router;


