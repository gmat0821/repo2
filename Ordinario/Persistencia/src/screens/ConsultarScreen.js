import React, { useState, useCallback } from 'react';
import { View, Text, FlatList, StyleSheet } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { supabase } from '../../supabase';

export default function ConsultarScreen() {
  const [lista, setLista] = useState([]);

  useFocusEffect(
    useCallback(() => {
      const obtenerInventario = async () => {
        const { data } = await supabase.from('Productos').select('*').order('id', { ascending: false });
        setLista(data || []);
      };
      obtenerInventario();
    }, [])
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={lista}
        keyExtractor={(item) => item.id.toString()}
        ListEmptyComponent={<Text style={styles.emptyText}>Sin registros disponibles</Text>}
        renderItem={({ item }) => (
          <View style={styles.itemBox}>
            <View style={{ flex: 1 }}>
              <Text style={styles.itemTitle}>{item.producto}</Text>
              {item.descripcion ? <Text style={styles.itemDesc}>{item.descripcion}</Text> : null}
            </View>
            <View style={styles.priceBadge}>
              <Text style={styles.priceText}>${parseFloat(item.precio).toFixed(2)}</Text>
            </View>
          </View>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, backgroundColor: '#0f172a' },
  itemBox: { flexDirection: 'row', backgroundColor: '#1e293b', padding: 16, marginBottom: 12, borderRadius: 12, alignItems: 'center', borderWidth: 1, borderColor: '#334155' },
  itemTitle: { fontSize: 17, fontWeight: 'bold', color: '#f8fafc' },
  itemDesc: { color: '#94a3b8', marginTop: 4, fontSize: 13 },
  priceBadge: { backgroundColor: '#10b98122', paddingHorizontal: 12, paddingVertical: 6, borderRadius: 20, borderWidth: 1, borderColor: '#10b981' },
  priceText: { color: '#10b981', fontWeight: 'bold', fontSize: 15 },
  emptyText: { color: '#64748b', textAlign: 'center', marginTop: 40, fontSize: 16 }
});