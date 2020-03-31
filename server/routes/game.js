const express = require('express');
const router = express.Router();
const Game = require('../bbdd/GameSchema');
const Map = require('../bbdd/MapSchema');
const Point = require('../bbdd/PointSchema');
const jwt = require('jsonwebtoken');
const verifyToken = require('../functMiddle/VerifyToken');
const Usuario = require('../bbdd/Usuario');

router.get('/', async (req, res) => {

    try {

        if (req.body.all) { // get all games stats of specific map/user
            if (req.body.all.user) { // from user
                Game.find({user: req.body.all.user}, function (err, games) {
                    if (err) res.json({ "error": err.message });
                    else res.json(games);
                });
            }
            else if (req.body.all.map) { // from map
                Game.find({map: req.body.all.map}, function (err, games) {
                    if (err) res.json({ "error": err.message });
                    else res.json(games);
                });
            }
        }
        else { // get one game stats
            if (req.body._id) { // specific game
                Game.findById(req.body._id, function (err, game) {
                    if (err) res.json({ "error": err.message });
                    else res.json(game);
                });
            }
            else if (req.body.user && req.body.map) { // last played by user in map

            }
            else if (req.body.user) { // last played by user
               
            }
            else if (req.body.map) { // last played in map

            }
        }
    } catch (err) {
        res.json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        if (!(req.body.location.lat && req.body.location.long)) throw new Error('No location sent');
        if (req.body.game) { // ID received: update a game: validate current point (and end game).
            
            const game = await Game.findById(req.body.game);
            if (game == null) throw new Error('Game does not exist');
            if (game.status != 'inProgress') throw new Error('Game is not in progress');

            const currentPoint = await Point.findById(game.progress[game.progress.length -1].point);
            
            if (distance(req.body.location.lat, req.body.location.long,
                currentPoint.coord.lat, currentPoint.coord.long) < process.env.ONFOOT_CHECK_DIST) { // point completed
                
                const date = new Date();
                game.progress[game.progress.length - 1].completedDate = date;
                const map = await Map.findById(game.map); // we suppose maps and points are well maintained

                if (map.points.length == game.progress.length) { // this is the last point validation
                    console.log('Last Point');
                    game.status = 'completed';
                    game.endDate = date;
                    res.json({
                        "status": game.status,
                        "time": parseTime(new Date(game.endDate - game.startDate))
                    })
                }
                else { // this is not the last point validation: send next.
                    console.log('Not Last Point');
                    const point = await Point.findById(map.points[game.progress.length]);
                    game.progress.push({ point: point._id });
                    res.json({
                        "description": point.description,
                        "coord": point.coord
                    });
                }
            }
            else {
                res.json({ "error": "Point is not completed yet" });
                game.progress[game.progress.length - 1].tries++;
            }
            await game.save();
        }
        else { // No ID received: start new game.
            const map = await Map.findById(req.body.map); // so we can dereference point 0
            if (map == null) throw new Error('Map does not exist'); // protect against nonexistant maps
            //if ( too far from start ) throw new Error('You ate too far from start point'); 
            const game = new Game({
                user: req.body.user,
                map: req.body.map,
                startCoord: {
                    lat: req.body.location.lat,
                    long: req.body.location.long,
                },
                progress: {
                    point: map.points[0], // point 0 dereferenced
                }
            });
            const savedGame = await game.save();
            if (savedGame.error) res.json(savedGame);
            else {
                const point = await Point.findById(map.points[0]); // we suppose points are well maintained
                res.json({
                    "_id": savedGame._id,
                    "description": point.description,
                    "coord": point.coord
                });
            }
        }
    } catch (err) {
        res.json({ "error": err.message });
    }
});

//Add game to the user falta mirar si ja existex etc etc
//
router.post('/add_game', verifyToken, async (req, res) => {
    try {
    
        const myquery  = {_id: req.usernameId._id};
        const newvalues = {'games': {_id: req.body._id, name: req.body.name, owner: req.body.owner, metadata: { author: req.body.metadata.author, description: req.body.metadata.description}, points: req.body.points}};
        const resultat = await Usuario.updateOne(req.usernameId_id, {$addToSet: newvalues}, function(err, res) {
            if (err) res.json( err.message);
            //console.log("1 document updated");
            
        });
        res.json(resultat); //cambiar
    }  catch (error) {
        res.json({ "error": error.message });
    }
});

module.exports = router;

function parseTime(milliseconds) {
    //Get hours from milliseconds
    var hours = milliseconds / (1000 * 60 * 60);
    var absoluteHours = Math.floor(hours);
    var h = absoluteHours > 9 ? absoluteHours : '0' + absoluteHours;

    //Get remainder from hours and convert to minutes
    var minutes = (hours - absoluteHours) * 60;
    var absoluteMinutes = Math.floor(minutes);
    var m = absoluteMinutes > 9 ? absoluteMinutes : '0' + absoluteMinutes;

    //Get remainder from minutes and convert to seconds
    var seconds = (minutes - absoluteMinutes) * 60;
    var absoluteSeconds = Math.floor(seconds);
    var s = absoluteSeconds > 9 ? absoluteSeconds : '0' + absoluteSeconds;

    return h + 'h' + m + 'm' + s + 's';
}

function distance(lat1, lon1, lat2, lon2) {
    var R = 6371; // km
    var dLat = toRad(lat2 - lat1);
    var dLon = toRad(lon2 - lon1);
    var lat1 = toRad(lat1);
    var lat2 = toRad(lat2);

    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = R * c;
    return d * 1000;
}

function toRad(Value) {
    return Value * Math.PI / 180;
}

