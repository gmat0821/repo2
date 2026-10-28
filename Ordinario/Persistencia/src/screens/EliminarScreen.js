import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { supabase } from '../../supabase';

export default function EliminarScreen() {
  const [data, setData] = useState([]);

  const obtenerDatos = async () => {
    const { data: res } = await supabase.from('Productos').select('*').order('id', { ascending: false });
    setData(res || []);
  };

  useFocusEffect(useCallback(() => { obtenerDatos(); }, []));

  const confirmarBorrado = (id, titulo) => {
    Alert.alert("Eliminar Producto", `¿Deseas remover "${titulo}" permanentemente?`, [
      { text: "No", style: "cancel" },
      { 
        text: "Sí, borrar", 
        style: "destructive", 
        onPress: async () => {
          const { error } = await supabase.from('Productos').delete().eq('id', id);
          if (error) Alert.alert("Error", error.message);
          else obtenerDatos();
        }
      }
    ]);
  };

  return (
    <View style={styles.container}>
      <FlatList
        data={data}
        keyExtractor={(item) => item.id.toString()}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={{ flex: 1 }}>
              <Text style={styles.title}>{item.producto}</Text>
              <Text style={styles.price}>${item.precio}</Text>
            </View>
            <TouchableOpacity style={styles.deleteBtn} onPress={() => confirmarBorrado(item.id, item.producto)}>
              <Text style={styles.deleteText}>Eliminar</Text>
            </TouchableOpacity>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, backgroundColor: '#0f172a' },
  card: { flexDirection: 'row', backgroundColor: '#1e293b', padding: 16, marginBottom: 10, borderRadius: 12, alignItems: 'center', borderWidth: 1, borderColor: '#334155' },
  title: { fontSize: 16, fontWeight: 'bold', color: '#f8fafc' },
  price: { color: '#94a3b8', marginTop: 2 },
  deleteBtn: { backgroundColor: '#ef444422', paddingHorizontal: 14, paddingVertical: 8, borderRadius: 8, borderWidth: 1, borderColor: '#ef4444' },
  deleteText: { color: '#ef4444', fontWeight: 'bold', fontSize: 13 }
});