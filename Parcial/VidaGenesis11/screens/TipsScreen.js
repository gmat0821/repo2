import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet } from 'react-native';

export default function TipsScreen() {
  const [amount, setAmount] = useState('');
  const [tipPercent, setTipPercent] = useState(15);

  const totalTip = amount ? (parseFloat(amount) * tipPercent) / 100 : 0;
  const totalBill = amount ? parseFloat(amount) + totalTip : 0;

  return (
    <View style={styles.container}>
      <View style={styles.card}>
        <Text style={styles.title}>Calculadora de Propinas</Text>
        <TextInput
          style={styles.input}
          placeholder="Monto de la cuenta ($)"
          keyboardType="numeric"
          value={amount}
          onChangeText={setAmount}
          placeholderTextColor="#9ca3af"
        />
        
        <View style={styles.row}>
          {[10, 15, 20].map((percent) => (
            <TouchableOpacity
              key={percent}
              style={[styles.percentButton, tipPercent === percent && styles.activePercent]}
              onPress={() => setTipPercent(percent)}
            >
              <Text style={[styles.percentText, tipPercent === percent && styles.activePercentText]}>
                {percent}%
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        <View style={styles.resultBox}>
          <Text style={styles.resultLabel}>Propina: ${totalTip.toFixed(2)}</Text>
          <Text style={styles.resultTotal}>Total a Pagar: ${totalBill.toFixed(2)}</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20, backgroundColor: '#F3F4F6' },
  card: { backgroundColor: '#fff', padding: 25, borderRadius: 20, elevation: 5, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 10 },
  title: { fontSize: 22, fontWeight: 'bold', color: '#1F2937', marginBottom: 20, textAlign: 'center' },
  input: { backgroundColor: '#F9FAFB', borderWidth: 1, borderColor: '#E5E7EB', borderRadius: 10, padding: 15, fontSize: 18, color: '#1F2937', marginBottom: 20, textAlign: 'center' },
  row: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 30 },
  percentButton: { flex: 1, backgroundColor: '#F3F4F6', padding: 15, borderRadius: 10, marginHorizontal: 5, alignItems: 'center' },
  activePercent: { backgroundColor: '#4F46E5' },
  percentText: { fontSize: 16, fontWeight: 'bold', color: '#4B5563' },
  activePercentText: { color: '#fff' },
  resultBox: { backgroundColor: '#1F2937', padding: 20, borderRadius: 15, alignItems: 'center' },
  resultLabel: { color: '#9CA3AF', fontSize: 18, marginBottom: 5 },
  resultTotal: { color: '#10B981', fontSize: 24, fontWeight: 'bold' },
});