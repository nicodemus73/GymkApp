const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
    console.log('You provived a token with user _id: ', req.usernameId._id);
    res.status(200).send('This is home');
});

module.exports = router;