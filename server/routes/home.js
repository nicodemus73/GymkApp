const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
    res.send("This is home. Docker Hub test 2");
});

module.exports = router;