import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <View style={styles.glowCircle}>
        <Ionicons name="terminal" size={90} color="#00E5FF" />
      </View>
      <Text style={styles.title}>VidaGenesis11</Text>
      <Text style={styles.subtitle}>SISTEMA INICIALIZADO</Text>
      <Text style={styles.body}>Abre el panel lateral para acceder a los módulos de Memorama, Propinas y la Zona Arcade.</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#121212', padding: 20 },
  glowCircle: { padding: 30, borderRadius: 100, backgroundColor: 'rgba(0, 229, 255, 0.05)', borderWidth: 1, borderColor: 'rgba(0, 229, 255, 0.3)', marginBottom: 20 },
  title: { fontSize: 32, fontWeight: '900', color: '#FFF', letterSpacing: 2 },
  subtitle: { fontSize: 14, color: '#FF007F', marginTop: 10, fontWeight: 'bold', letterSpacing: 4 },
  body: { fontSize: 16, color: '#888', marginTop: 30, textAlign: 'center', lineHeight: 24 }
});