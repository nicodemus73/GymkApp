const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
    res.send("This is the user route");
});

router.get('/hola', (req, res) => {
    res.send("hello");
});

router.get('/adeu', (req, res) => {
    res.send("adeu");
});

/*router.get('/add_user', async(req, res) => {
    console.log(new Task(req.body)); //crea el objeto a almacenar
    const task = new Task(req.body);
    await task.save();
    res.send(new Task(req.body));
});*/

module.exports = router;