// componentes/resumenDelDia.js
import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { colors } from '../config/designSystem';

export default function ResumenDelDia({ route, navigation }) {
  const { diaNumero = 1, nombreDia = 'Lunes' } = route.params || {};

  const [comidas, setComidas] = useState([
    { id: 1, nombre: 'Desayuno', completado: true },
    { id: 2, nombre: 'Colacion', completado: true },
    { id: 3, nombre: 'Comida', completado: true },
    { id: 4, nombre: 'Colacion', completado: false },
    { id: 5, nombre: 'Cena', completado: true },
  ]);

  const toggleCompletado = (id) => {
    setComidas(comidas.map(comida => 
      comida.id === id ? { ...comida, completado: !comida.completado } : comida
    ));
  };

  const comidasCompletadas = comidas.filter(c => c.completado).length;

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={styles.monthText}>← SEPTIEMBRE</Text>
        </TouchableOpacity>
        
        <View style={styles.titleRow}>
          <Text style={styles.dayTitle}>{nombreDia} {diaNumero}</Text>
          <Text style={styles.progressText}>{comidasCompletadas} / {comidas.length}</Text>
        </View>
      </View>

      <View style={styles.body}>
        {comidas.map((item) => (
          <TouchableOpacity 
            key={item.id} 
            style={styles.listItem}
            onPress={() => toggleCompletado(item.id)}
            activeOpacity={0.7}
          >
            <View style={[styles.checkbox, item.completado ? styles.checkboxChecked : styles.checkboxMissed]}>
              <Text style={{ color: item.completado ? '#fff' : 'gray', fontWeight: 'bold' }}>
                {item.completado ? '✓' : '×'}
              </Text>
            </View>
            <Text style={[styles.itemText, !item.completado && styles.itemTextMissed]}>{item.nombre}</Text>
          </TouchableOpacity>
        ))}

        <View style={styles.waterContainer}>
          <Text style={styles.waterLabel}>AGUA</Text>
          <Text style={styles.waterValue}>2.0 L</Text>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { 
    flex: 1, 
    backgroundColor: colors.background 
    // Sin margen ni borde exterior para pantalla completa
  },
  header: { 
    paddingHorizontal: 20, 
    paddingVertical: 15, 
    borderBottomWidth: 2, 
    borderColor: colors.black 
  },
  monthText: { color: colors.primary.DEFAULT, fontWeight: 'bold', fontSize: 10, letterSpacing: 1, paddingVertical: 5 },
  titleRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: 10 },
  dayTitle: { fontSize: 24, fontWeight: 'bold', color: colors.black },
  progressText: { fontSize: 12, fontWeight: 'bold', color: colors.black },
  body: { padding: 20, flex: 1 },
  listItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 15, borderBottomWidth: 2, borderColor: colors.black },
  checkbox: { width: 24, height: 24, borderWidth: 2, borderColor: colors.black, justifyContent: 'center', alignItems: 'center', marginRight: 15 },
  checkboxChecked: { backgroundColor: colors.primary.dark },
  checkboxMissed: { backgroundColor: 'transparent', borderColor: 'gray' },
  itemText: { fontSize: 16, fontWeight: 'bold', color: colors.black },
  itemTextMissed: { color: 'gray' },
  waterContainer: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 30, paddingTop: 15 },
  waterLabel: { color: colors.primary.DEFAULT, fontWeight: 'bold', letterSpacing: 1 },
  waterValue: { fontSize: 18, fontWeight: 'bold', color: colors.black }
});