import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, TextInput, StyleSheet, Alert, TouchableOpacity } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { supabase } from '../../supabase';


export default function ActualizarScreen() {
  const [items, setItems] = useState([]);
  const [editId, setEditId] = useState(null);
  
  const [nom, setNom] = useState('');
  const [desc, setDesc] = useState('');
  const [prec, setPrec] = useState('');

  const refrescar = async () => {
    const { data } = await supabase.from('Productos').select('*').order('id', { ascending: false });
    setItems(data || []);
  };

  useFocusEffect(useCallback(() => { refrescar(); setEditId(null); }, []));

  const iniciarEdicion = (elem) => {
    setEditId(elem.id);
    setNom(elem.producto);
    setDesc(elem.descripcion || '');
    setPrec(elem.precio.toString());
  };

  const guardarCambios = async () => {
    const { error } = await supabase
      .from('Productos')
      .update({ producto: nom, descripcion: desc, precio: parseFloat(prec) })
      .eq('id', editId);
    
    if (error) {
      Alert.alert('Error', error.message);
    } else {
      Alert.alert('Actualizado', 'Los datos fueron modificados.');
      setEditId(null);
      refrescar();
    }
  };

  return (
    <View style={styles.container}>
      {editId ? (
        <View style={styles.modalBox}>
          <Text style={styles.modalTitle}>Editar Registro #{editId}</Text>
          <TextInput style={styles.input} value={nom} onChangeText={setNom} placeholder="Nombre" placeholderTextColor="#777" />
          <TextInput style={styles.input} value={desc} onChangeText={setDesc} placeholder="Descripción" placeholderTextColor="#777" />
          <TextInput style={styles.input} value={prec} onChangeText={setPrec} keyboardType="decimal-pad" placeholder="Precio" placeholderTextColor="#777" />
          
          <View style={styles.rowBtn}>
            <TouchableOpacity style={styles.btnCancel} onPress={() => setEditId(null)}>
              <Text style={styles.btnText}>Descartar</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.btnSave} onPress={guardarCambios}>
              <Text style={styles.btnText}>Guardar</Text>
            </TouchableOpacity>
          </View>
        </View>
      ) : (
        <FlatList
          data={items}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <TouchableOpacity style={styles.rowCard} onPress={() => iniciarEdicion(item)}>
              <View style={{ flex: 1 }}>
                <Text style={styles.cardTitle}>{item.producto}</Text>
                <Text style={styles.cardSub}>${item.precio}</Text>
              </View>
              <Text style={styles.actionTag}>Editar ✏️</Text>
            </TouchableOpacity>
          )}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, backgroundColor: '#0f172a' },
  modalBox: { backgroundColor: '#1e293b', padding: 20, borderRadius: 16, borderWidth: 1, borderColor: '#334155' },
  modalTitle: { fontSize: 18, color: '#f8fafc', fontWeight: 'bold', marginBottom: 15 },
  input: { backgroundColor: '#0f172a', color: '#fff', padding: 12, marginBottom: 12, borderRadius: 8, borderWidth: 1, borderColor: '#334155' },
  rowBtn: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 10 },
  btnCancel: { backgroundColor: '#64748b', padding: 14, borderRadius: 8, flex: 0.48, alignItems: 'center' },
  btnSave: { backgroundColor: '#3b82f6', padding: 14, borderRadius: 8, flex: 0.48, alignItems: 'center' },
  btnText: { color: '#fff', fontWeight: 'bold' },
  rowCard: { flexDirection: 'row', backgroundColor: '#1e293b', padding: 16, marginBottom: 10, borderRadius: 12, alignItems: 'center', borderWidth: 1, borderColor: '#334155' },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#f8fafc' },
  cardSub: { color: '#94a3b8', marginTop: 2 },
  actionTag: { color: '#3b82f6', fontWeight: '600' }
});