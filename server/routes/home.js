const express = require('express');
const distance = require('turf-distance');
const router = express.Router();

router.get('/', (req, res) => {
    console.log('You provived a token with user _id: ', req.usernameId._id);
    res.status(200).send('This is home');
});

router.post('/', (req, res) => {
    console.log(distance(req.body.location1, req.body.location2));
    res.status(200).send('This is home');
});

module.exports = router;