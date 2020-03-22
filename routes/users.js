const express = require('express');
const router = express.Router();


// Routes
router.get('/', (req, res) => {
    res.send('We are on users');
});

router.get('/specific', (req, res) => {
    res.send('We are on specific users');
});

module.exports = router;