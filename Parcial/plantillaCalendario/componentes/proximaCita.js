// componentes/proximaCita.js
import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { colors } from '../config/designSystem';

export default function ProximaCita({ navigation }) {
  // Estado para controlar si ya se agregó al calendario
  const [agregado, setAgregado] = useState(false);

  const toggleCalendario = () => {
    if (!agregado) {
      setAgregado(true);
      Alert.alert(
        "¡Cita Agregada!",
        "La cita se ha guardado exitosamente en tu calendario."
      );
    } else {
      setAgregado(false);
      Alert.alert(
        "Cita Removida",
        "Se ha quitado la cita de tu calendario."
      );
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>← SEPTIEMBRE</Text>
        </TouchableOpacity>
        
        <Text style={styles.title}>Tu proxima cita</Text>
      </View>

      <View style={styles.body}>
        <Text style={styles.dateAccent}>JUEVES 10 DE SEPTIEMBRE</Text>
        <Text style={styles.timeBig}>11:00</Text>

        <View style={styles.infoRow}>
          <Text style={styles.label}>CON</Text>
          <Text style={styles.value}>Nut. Jorge M.</Text>
        </View>

        <View style={styles.infoRow}>
          <Text style={styles.label}>DONDE</Text>
          <Text style={styles.value}>Consultorio 2</Text>
        </View>

        {/* Botón dinámico según el estado */}
        <TouchableOpacity 
          style={[styles.button, agregado && styles.buttonAgregado]} 
          onPress={toggleCalendario}
          activeOpacity={0.8}
        >
          <Text style={[styles.buttonText, agregado && styles.buttonTextAgregado]}>
            {agregado ? '✓ AGREGADO AL CALENDARIO' : 'AGREGAR AL CALENDARIO'}
          </Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { 
    flex: 1, 
    backgroundColor: colors.background 
    // Se eliminaron margin: 10 y borderWidth: 2 para ocupar la pantalla completa
  },
  header: { 
    paddingHorizontal: 20, 
    paddingVertical: 15, 
    borderBottomWidth: 2, 
    borderColor: colors.black 
  },
  backText: { 
    color: colors.primary.DEFAULT, 
    fontWeight: 'bold', 
    fontSize: 10, 
    letterSpacing: 1, 
    paddingVertical: 5 
  },
  title: { 
    fontSize: 24, 
    fontWeight: 'bold', 
    color: colors.black, 
    marginTop: 5 
  },
  body: { 
    padding: 20, 
    flex: 1 
  },
  dateAccent: { 
    color: colors.accent.DEFAULT, 
    fontWeight: 'bold', 
    letterSpacing: 1, 
    fontSize: 12 
  },
  timeBig: { 
    fontSize: 60, 
    fontWeight: 'bold', 
    color: colors.black, 
    marginVertical: 10, 
    letterSpacing: -2 
  },
  infoRow: { 
    flexDirection: 'row', 
    paddingVertical: 20, 
    borderBottomWidth: 2, 
    borderColor: colors.black, 
    alignItems: 'center' 
  },
  label: { 
    width: 80, 
    color: colors.primary.DEFAULT, 
    fontWeight: 'bold', 
    fontSize: 12, 
    letterSpacing: 1 
  },
  value: { 
    fontSize: 16, 
    fontWeight: 'bold', 
    color: colors.black 
  },
  button: { 
    marginTop: 40, 
    padding: 15, 
    borderWidth: 2, 
    borderColor: colors.black, 
    backgroundColor: 'transparent' 
  },
  buttonAgregado: {
    backgroundColor: colors.primary.dark,
    borderColor: colors.primary.dark
  },
  buttonText: { 
    textAlign: 'center', 
    fontWeight: 'bold', 
    color: colors.black, 
    letterSpacing: 1 
  },
  buttonTextAgregado: {
    color: '#FFFFFF'
  }
});