// data.js
export let goals = [];
export let members = [];
export let payments = [];
export let nextId = 1;

export function getNextId() {
  return String(nextId++);
}