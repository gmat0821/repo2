import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, ScrollView } from 'react-native';
import { supabase } from '../../supabase';

export default function AgregarScreen() {
  const [producto, setProducto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [precio, setPrecio] = useState('');

  const registrarItem = async () => {
    if (!producto.trim() || !precio.trim()) {
      Alert.alert('Atención', 'Nombre y precio son requeridos');
      return;
    }

    const { error } = await supabase
      .from('Productos')
      .insert([{ producto, descripcion, precio: parseFloat(precio) }]);
    
    if (error) {
      Alert.alert('Error', error.message);
    } else {
      Alert.alert('Éxito', '¡Producto guardado exitosamente!');
      setProducto('');
      setDescripcion('');
      setPrecio('');
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.card}>
        <Text style={styles.header}>Alta de Artículo</Text>
        
        <Text style={styles.label}>Nombre del Producto</Text>
        <TextInput 
          style={styles.input} 
          placeholder="Ej. Teclado Mecánico" 
          placeholderTextColor="#777"
          value={producto} 
          onChangeText={setProducto} 
        />

        <Text style={styles.label}>Descripción</Text>
        <TextInput 
          style={[styles.input, styles.textArea]} 
          placeholder="Detalles del artículo..." 
          placeholderTextColor="#777"
          multiline
          value={descripcion} 
          onChangeText={setDescripcion} 
        />

        <Text style={styles.label}>Precio ($)</Text>
        <TextInput 
          style={styles.input} 
          placeholder="0.00" 
          placeholderTextColor="#777"
          value={precio} 
          onChangeText={setPrecio} 
          keyboardType="decimal-pad" 
        />

        <TouchableOpacity style={styles.btnMain} onPress={registrarItem}>
          <Text style={styles.btnText}>+ Agregar a Inventario</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: 20, backgroundColor: '#0f172a', justifyContent: 'center' },
  card: { backgroundColor: '#1e293b', padding: 22, borderRadius: 16, borderWidth: 1, borderColor: '#334155' },
  header: { fontSize: 22, fontWeight: 'bold', color: '#f8fafc', marginBottom: 20, textAlign: 'center' },
  label: { color: '#94a3b8', fontSize: 13, marginBottom: 6, fontWeight: '600' },
  input: { backgroundColor: '#0f172a', color: '#fff', padding: 14, marginBottom: 16, borderRadius: 10, borderWidth: 1, borderColor: '#334155' },
  textArea: { height: 80, textAlignVertical: 'top' },
  btnMain: { backgroundColor: '#6366f1', padding: 16, borderRadius: 10, alignItems: 'center', marginTop: 10 },
  btnText: { color: '#fff', fontWeight: 'bold', fontSize: 16 }
});