const express = require('express');
const router = express.Router();

const Task = require('../bbdd/Task'); //per poder utilitzar la bbdd

router.get('/', (req, res) => {
    res.send("This is the user route");
});

router.get('/hola', (req, res) => {
    res.send("hello");
});

router.get('/adeu', (req, res) => {
    res.send("adeu");
});

router.post('/add', async(req, res) => { //guardar es un exemple, a millorar

    const task = new Task({
        title: req.body.title,
        author: req.body.author,
        description: req.body.description
    });
    try {
        const savedUser = await task.save();
        console.log(task);
        console.log(req.body.title);
        res.json(savedUser);
    } catch(err) {
        res.json({ message: err});
    }
});

module.exports = router;