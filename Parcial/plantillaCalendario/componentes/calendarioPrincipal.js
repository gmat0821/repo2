// componentes/calendarioPrincipal.js
import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { colors } from '../config/designSystem';

export default function CalendarioPrincipal({ navigation }) {
  const diasSemanaHeader = ['L', 'M', 'M', 'J', 'V', 'S', 'D'];
  const nombresDiasCompletos = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
  const diasMes = Array.from({length: 28}, (_, i) => i + 1); 

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.topBar}>
        <Text style={styles.topBarText}>9:41</Text>
        <Text style={styles.topBarText}>CHECK-EAT</Text>
        <Text style={styles.topBarText}>100%</Text>
      </View>

      <View style={styles.header}>
        <Text style={styles.yearText}>2026</Text>
        <Text style={styles.monthText}>Septiembre</Text>
      </View>

      <View style={styles.body}>
        <View style={styles.weekRow}>
          {diasSemanaHeader.map((dia, index) => (
            <Text key={index} style={styles.weekDayText}>{dia}</Text>
          ))}
        </View>

        <View style={styles.grid}>
          {diasMes.map((dia) => {
            let diaStyle = styles.dayCell;
            let textStyle = styles.dayText;
            
            if(dia === 1 || dia === 2) diaStyle = [styles.dayCell, { backgroundColor: 'rgba(66,149,111,.30)' }];
            if(dia === 3) { diaStyle = [styles.dayCell, { backgroundColor: colors.primary.dark }]; textStyle = [styles.dayText, { color: '#fff' }]; }
            if(dia === 10) textStyle = [styles.dayText, { color: colors.accent.DEFAULT }];

            const nombreDia = nombresDiasCompletos[(dia - 1) % 7];

            return (
              <TouchableOpacity 
                key={dia} 
                style={diaStyle}
                onPress={() => navigation.navigate('ResumenDelDia', { 
                  diaNumero: dia, 
                  nombreDia: nombreDia 
                })}
              >
                <Text style={textStyle}>{dia}</Text>
                {dia === 10 && <View style={styles.blueDot} />}
              </TouchableOpacity>
            );
          })}
        </View>

        <View style={styles.adherenceContainer}>
          <Text style={styles.adherenceLabel}>ADHERENCIA DEL MES</Text>
          <Text style={styles.adherenceValue}>82%</Text>
        </View>
        
        <TouchableOpacity 
          style={{ marginTop: 20 }}
          onPress={() => navigation.navigate('ProximaCita')}
        >
          <Text style={styles.linkText}>VER MI PROXIMA CITA</Text>
        </TouchableOpacity>
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
  topBar: { 
    flexDirection: 'row', 
    justify: 'space-between', 
    paddingHorizontal: 20, 
    paddingVertical: 10, 
    borderBottomWidth: 2, 
    borderColor: colors.black 
  },
  topBarText: { fontSize: 10, fontWeight: 'bold' },
  header: { 
    paddingHorizontal: 20, 
    paddingVertical: 15, 
    borderBottomWidth: 2, 
    borderColor: colors.black 
  },
  yearText: { color: colors.primary.dark, fontWeight: 'bold', fontSize: 12, letterSpacing: 2 },
  monthText: { fontSize: 32, fontWeight: 'bold', marginTop: 5, color: colors.black },
  body: { padding: 20, flex: 1 },
  weekRow: { flexDirection: 'row', justifyContent: 'space-around', marginBottom: 10 },
  weekDayText: { color: colors.primary.DEFAULT, fontWeight: 'bold', width: 34, textAlign: 'center' },
  grid: { flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-around' },
  dayCell: { width: 40, height: 40, justifyContent: 'center', alignItems: 'center', marginVertical: 5 },
  dayText: { fontSize: 14, color: colors.black },
  blueDot: { width: 5, height: 5, backgroundColor: colors.accent.DEFAULT, marginTop: 2 },
  adherenceContainer: { flexDirection: 'row', justifyContent: 'space-between', borderTopWidth: 2, borderColor: colors.black, paddingTop: 20, marginTop: 30 },
  adherenceLabel: { color: colors.primary.DEFAULT, fontWeight: 'bold', letterSpacing: 1 },
  adherenceValue: { fontSize: 18, fontWeight: 'bold', color: colors.black },
  linkText: { color: colors.accent.DEFAULT, fontWeight: 'bold', textAlign: 'center', letterSpacing: 1 }
});