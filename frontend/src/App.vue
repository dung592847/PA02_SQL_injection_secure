<script setup>
import { ref } from 'vue'

const username = ref('')
const password = ref('')
const message = ref('')

const login = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value
      })
    })
    
    const text = await response.text()
    message.value = text
  } catch (error) {
    message.value = 'Error: ' + error.message
  }
}
</script>

<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit">Login</button>
    </form>
    <p v-if="message" class="message">{{ message }}</p>
  </div>
</template>

<style scoped>
.login-container {
  max-width: 300px;
  margin: 0 auto;
  padding: 2em;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #f9f9f9; 
  /* slightly light background for better visibility against white/dark theme */
}
.form-group {
  margin-bottom: 1em;
}
label {
  display: block;
  margin-bottom: 0.5em;
  color: #333;
}
input {
  width: 100%;
  padding: 0.5em;
  border: 1px solid #ddd;
  border-radius: 4px;
}
button {
  width: 100%;
  padding: 0.7em;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #3aa876;
}
.message {
  margin-top: 1em;
  font-weight: bold;
  text-align: center;
  color: #333;
}
</style>
