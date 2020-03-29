const express = require('express');
const router = express.Router();
const Game = require('../bbdd/GameSchema');
const Map = require('../bbdd/MapSchema');
const Point = require('../bbdd/PointSchema');

router.get('/', (req, res) => {

    try {

        // get status of last game played by user (in specific map)
        // get all games played by user
        // get stats of specific game

    } catch (err) {
        res.json({ "error": err.message });
    }
});

router.post('/', async (req, res) => {

    try {
        if (req.body.game) { // ID received: update a game: validate current point (and end game).
            const game = await Game.findByIdAndUpdate(req.body.game);
            if (game == null) throw new Error('Game does not exist');
            if (game.status != 'inProgress') throw new Error('Game is not in progress');
            if (/*punt vàlid*/ true){
                const map = await Map.findById(game.map); // we suppose maps are well maintained
                const date = new Date();

                // no funciona encara.
                // mirar millor findByIdAndUpdate()
                console.log(date);
                game.progress[game.progress.length -1].completedDate = date;

                if (map.points.length -1 == game.progress.length){ // this is the last point
                    console.log('Last Point');
                }
                else{ // this is not the last point
                    console.log('Not Last Point');
                }
            }
            else{
                res.json({ "error": "Point is not completed"});
            }
        }
        else { // No ID received: start new game.
            const map = await Map.findById(req.body.map); // so we can dereference point 0
            if (map == null) throw new Error('Map does not exist'); // protect against nonexistant maps
            //if ( too far from start ) throw new Error('You ate too far from start point'); 
            const game = new Game({
                user: req.body.user,
                map: req.body.map,
                startCoord: {
                    lat: req.body.startCoord.lat,
                    long: req.body.startCoord.long,
                },
                progress: {
                    point: map.points[0], // point 0 dereferenced
                    tries: 1,
                }
            });
            const savedGame = await game.save();
            if (savedGame.error) res.json(savedGame);
            else{
                const point = await Point.findById(map.points[0]); // we suppose points are well maintained
                res.json({
                    "description": point.description,
                    "coord": point.coord
                });
            }
        }
    } catch (err) {
        res.json({ "error": err.message });
    }

});

module.exports = router;