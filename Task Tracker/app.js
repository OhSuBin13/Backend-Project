#!/usr/bin/env node

const fs = require("fs");
const path = require("path");

const DATA_FILE = path.join(process.cwd(), "tasks.json");

function printUsage() {
  console.log(`Usage:
  task-cli add "<description>"`);
}

function exitWithError(message) {
  console.error(`Error: ${message}`);
  printUsage();
  process.exit(1);
}

function ensureDataFile() {
  if (!fs.existsSync(DATA_FILE)) {
    fs.writeFileSync(DATA_FILE, "[]\n", "utf8");
  }
}

function loadTasks() {
  ensureDataFile();

  try {
    const raw = fs.readFileSync(DATA_FILE, "utf8").trim();
    if (raw === "") {
      return [];
    }

    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) {
      throw new Error("Task data must be an array.");
    }

    return parsed;
  } catch (error) {
    console.error(`Error: Failed to read task data from ${DATA_FILE}.`);
    console.error(`Reason: ${error.message}`);
    process.exit(1);
  }
}

function saveTasks(tasks) {
  fs.writeFileSync(DATA_FILE, `${JSON.stringify(tasks, null, 2)}\n`, "utf8");
}

function validateDescription(value, action) {
  const description = (value || "").trim();
  if (!description) {
    exitWithError(`A non-empty description is required to ${action}.`);
  }
  return description;
}

function getNextId(tasks) {
  return tasks.reduce((maxId, task) => Math.max(maxId, task.id || 0), 0) + 1;
}

function addTask(tasks, description) {
  const timestamp = new Date().toISOString();
  const task = {
    id: getNextId(tasks),
    description,
    status: "todo",
    createdAt: timestamp,
    updatedAt: timestamp,
  };

  tasks.push(task);
  saveTasks(tasks);
  console.log(`Task added successfully (ID: ${task.id})`);
}

function main() {
  const [, , command, ...args] = process.argv;

  if (!command) {
    exitWithError("A command is required.");
  }

  const tasks = loadTasks();

  switch (command) {
    case "add":
      if (args.length < 1) {
        exitWithError("Missing task description.");
      }
      addTask(tasks, validateDescription(args.join(" "), "add"));
      break;

    default:
      exitWithError(`Unknown command: ${command}`);
  }
}

main();
