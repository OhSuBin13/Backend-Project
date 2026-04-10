import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import dotenv from "dotenv";

dotenv.config();

export async function generateHashPassword(plainPassword) {
  try {
    const hashedPassword = await bcrypt.hash(
      plainPassword,
      parseInt(process.env.SALT),
    );
    return hashedPassword;
  } catch (error) {
    console.error("Error hashing password:", error);
    throw new Error("Could not hash password");
  }
}

export function jwttokengenerator(user) {
  return jwt.sign(user, process.env.SECRET_KEY);
}

export async function comparePassword(plainPassword, hash) {
  const result = await bcrypt.compare(plainPassword, hash);
  return result;
}

export function verifytoken(token) {
  const verified = jwt.verify(token, process.env.SECRET_KEY);
  return verified;
}
