const express = require('express');
const router = express.Router();


// Routes
router.get('/', (req, res) => {
    res.send('We are on posts');
});

router.get('/specific', (req, res) => {
    res.send('We are on specific posts');
});

router.post('/', (req, res) => {
    console.log(req.body);
});

module.exports = router;