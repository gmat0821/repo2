import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform } from 'react-native';

export default function BMIScreen() {
  const [weight, setWeight] = useState('');
  const [height, setHeight] = useState('');
  const [result, setResult] = useState(null);
  const [status, setStatus] = useState('');

  const calculateBMI = () => {
    const w = parseFloat(weight);
    const h = parseFloat(height) / 100;
    if (w > 0 && h > 0) {
      const bmi = (w / (h * h)).toFixed(1);
      setResult(bmi);
      if (bmi < 18.5) setStatus('Bajo peso');
      else if (bmi < 24.9) setStatus('Peso normal');
      else if (bmi < 29.9) setStatus('Sobrepeso');
      else setStatus('Obesidad');
    }
  };

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.container}>
      <View style={styles.card}>
        <Text style={styles.title}>Calculadora de IMC</Text>
        <TextInput
          style={styles.input}
          placeholder="Peso en kg (ej. 70)"
          keyboardType="numeric"
          value={weight}
          onChangeText={setWeight}
          placeholderTextColor="#9ca3af"
        />
        <TextInput
          style={styles.input}
          placeholder="Altura en cm (ej. 175)"
          keyboardType="numeric"
          value={height}
          onChangeText={setHeight}
          placeholderTextColor="#9ca3af"
        />
        <TouchableOpacity style={styles.button} onPress={calculateBMI}>
          <Text style={styles.buttonText}>Calcular</Text>
        </TouchableOpacity>
        
        {result && (
          <View style={styles.resultContainer}>
            <Text style={styles.resultText}>IMC: {result}</Text>
            <Text style={styles.statusText}>{status}</Text>
          </View>
        )}
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20, backgroundColor: '#F3F4F6' },
  card: { backgroundColor: '#fff', padding: 30, borderRadius: 20, elevation: 5, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 10 },
  title: { fontSize: 24, fontWeight: 'bold', color: '#1F2937', marginBottom: 20, textAlign: 'center' },
  input: { backgroundColor: '#F9FAFB', borderWidth: 1, borderColor: '#E5E7EB', borderRadius: 10, padding: 15, marginBottom: 15, fontSize: 16, color: '#1F2937' },
  button: { backgroundColor: '#4F46E5', padding: 15, borderRadius: 10, alignItems: 'center' },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' },
  resultContainer: { marginTop: 25, alignItems: 'center', padding: 15, backgroundColor: '#EEF2FF', borderRadius: 10 },
  resultText: { fontSize: 22, fontWeight: 'bold', color: '#4F46E5' },
  statusText: { fontSize: 18, color: '#4338CA', marginTop: 5 },
});