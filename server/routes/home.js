const express = require('express');
const distance = require('turf-distance');
const router = express.Router();

router.get('/', (req, res) => {
    res.status(200).send('This is home GET');
});

router.post('/', (req, res) => {
    res.status(200).send('This is home POST');
});

module.exports = router;