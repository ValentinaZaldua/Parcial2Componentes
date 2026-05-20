// app.js
import express from 'express';
import { goals, members, payments, getNextId } from './data.js';

const app = express()
const port = 3000

app.use(express.json())

app.get('/', (req, res) => {
  res.send('Servidor de ahorro familiar corriendo!')
})

// Lista todas las metas
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

// Detalle de una meta
app.get('/goals/:id', (req, res) => {
  const goal = goals.find(g => g.id === req.params.id)
  if (!goal) return res.status(404).send({ error: 'Meta no encontrada' })

  const goalMembers = members.filter(m => m.goalId === goal.id)
  const totalSaved = payments
    .filter(p => p.goalId === goal.id)
    .reduce((sum, p) => sum + p.amount, 0)

  res.send({ ...goal, members: goalMembers, totalSaved })
})

// Crea una meta
app.post('/goals', (req, res) => {
  const { name, description, totalValue, targetDate, imageUrl } = req.body
  const goal = {
    id: getNextId(),
    name,
    description,
    totalValue,
    targetDate,
    imageUrl: imageUrl || null
  }
  goals.push(goal)
  res.send(goal)
})

// Agrega un miembro
app.post('/members', (req, res) => {
  const { name, goalId } = req.body
  const member = { id: getNextId(), name, goalId }
  members.push(member)
  res.send(member)
})

// Pagos de una meta
app.get('/goals/:id/payments', (req, res) => {
  const result = payments.filter(p => p.goalId === req.params.id)
  res.send(result)
})

// Registra un pago
app.post('/payments', (req, res) => {
  const { memberId, goalId, amount, method, description } = req.body
  const payment = {
    id: getNextId(),
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