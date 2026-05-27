import express from 'express';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { goals, members, payments, getNextId } from './data.js';

const app = express()
const port = 3000

app.use(express.json())

// Carpeta "recursos" es pública — el front puede pedir imágenes con GET
app.use('/recursos', express.static('recursos'))

// Configuración de multer — guarda la imagen en recursos/{goalId}/
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const goalId = req.params.id
    const folder = `recursos/${goalId}`
    // Crea la carpeta si no existe
    if (!fs.existsSync(folder)) fs.mkdirSync(folder, { recursive: true })
    cb(null, folder)
  },
  filename: (req, file, cb) => {
    // Siempre se llama "image" + extensión original
    const ext = path.extname(file.originalname)
    cb(null, `image${ext}`)
  }
})
const upload = multer({ storage })

app.get('/', (req, res) => {
  res.send('Servidor de ahorro familiar corriendo!')
})

// ── METAS ──────────────────────────────────────────

app.get('/goals', (req, res) => {
  const result = goals.map(goal => {
    const goalMembers = members.filter(m => m.goalId === goal.id)
    const totalSaved = payments
      .filter(p => p.goalId === goal.id)
      .reduce((sum, p) => sum + p.amount, 0)
    return { ...goal, members: goalMembers, totalSaved }
  })
  res.send(result)
})

app.get('/goals/:id', (req, res) => {
  const goal = goals.find(g => g.id === req.params.id)
  if (!goal) return res.status(404).send({ error: 'Meta no encontrada' })

  const goalMembers = members.filter(m => m.goalId === goal.id)
  const totalSaved = payments
    .filter(p => p.goalId === goal.id)
    .reduce((sum, p) => sum + p.amount, 0)

  res.send({ ...goal, members: goalMembers, totalSaved })
})

app.post('/goals', (req, res) => {
  const { name, description, totalValue, targetDate } = req.body
  const goal = {
    id: String(getNextId()),
    name,
    description,
    totalValue,
    targetDate,
    imageUrl: null   // se sube después con el endpoint de imagen
  }
  goals.push(goal)
  res.send(goal)
})

// POST /goals/:id/image — sube la imagen de una meta
// El front envía un multipart/form-data con campo "image"
app.post('/goals/:id/image', upload.single('image'), (req, res) => {
  const goal = goals.find(g => g.id === req.params.id)
  if (!goal) return res.status(404).send({ error: 'Meta no encontrada' })

  // Guarda la URL pública de la imagen en la meta
  const ext = path.extname(req.file.originalname)
  goal.imageUrl = `http://192.168.20.33:3000/recursos/${req.params.id}/image${ext}`

  res.send({ imageUrl: goal.imageUrl })
})

// ── MIEMBROS ───────────────────────────────────────

app.post('/members', (req, res) => {
  const { name, goalId } = req.body
  const member = { id: String(getNextId()), name, goalId }
  members.push(member)
  res.send(member)
})

// ── PAGOS ──────────────────────────────────────────

app.get('/goals/:id/payments', (req, res) => {
  const result = payments.filter(p => p.goalId === req.params.id)
  res.send(result)
})

app.post('/payments', (req, res) => {
  const { memberId, goalId, amount, method, description } = req.body
  const payment = {
    id: String(getNextId()),
    memberId,
    goalId,
    amount,
    method,
    description: description || '',
    date: new Date().toISOString()
  }
  payments.push(payment)
  res.send(payment)
})

app.listen(port, () => {
  console.log(`App listening on port ${port}`)
})