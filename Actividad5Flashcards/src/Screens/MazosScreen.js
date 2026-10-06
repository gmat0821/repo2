import React, { useState } from 'react';
import { View, Text, TextInput, Button, FlatList, TouchableOpacity, StyleSheet } from 'react-native';
import CustomModal from '../../Componentes/CustomModal';

export default function MazosScreen({ mazos, setMazos }) {
  const [mazoActivo, setMazoActivo] = useState(null); 
  const [nombreMazo, setNombreMazo] = useState('');
  const [pregunta, setPregunta] = useState('');
  const [respuesta, setRespuesta] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [tarjetaSeleccionada, setTarjetaSeleccionada] = useState(null);
  
  // Nuevo estado para la pantalla de confirmación
  const [mazoAEliminar, setMazoAEliminar] = useState(null);

  const agregarMazo = () => {
    if (nombreMazo !== '') {
      const nuevoMazo = { id: Date.now().toString(), nombre: nombreMazo, tarjetas: [] };
      setMazos([...mazos, nuevoMazo]);
      setNombreMazo('');
    }
  };

  const agregarTarjeta = () => {
    if (pregunta !== '' && respuesta !== '') {
      const nuevaTarjeta = { id: Date.now().toString(), pregunta, respuesta };
      const mazosActualizados = mazos.map(mazo => {
        if (mazo.id === mazoActivo.id) {
          return { ...mazo, tarjetas: [...mazo.tarjetas, nuevaTarjeta] };
        }
        return mazo;
      });
      setMazos(mazosActualizados);
      setMazoActivo({ ...mazoActivo, tarjetas: [...mazoActivo.tarjetas, nuevaTarjeta] });
      setPregunta('');
      setRespuesta('');
    }
  };

  const confirmarEliminacion = () => {
    const nuevosMazos = mazos.filter(m => m.id !== mazoAEliminar.id);
    setMazos(nuevosMazos);
    setMazoAEliminar(null);
  };

  // Pantalla de confirmación para eliminar mazo
  if (mazoAEliminar) {
    return (
      <View style={styles.containerCenter}>
        <Text style={styles.emojiTriste}>(╥﹏╥)</Text>
        <Text style={styles.header}>¿Te gustaría eliminar el mazo?</Text>
        <Text style={styles.headerTitle}>"{mazoAEliminar.nombre}"</Text>
        
        <View style={styles.botonesFinales}>
          <View style={styles.btnContainer}>
            <Button title="Sí, eliminar 🗑️" onPress={confirmarEliminacion} color="#BA5A5A" />
          </View>
          <View style={[styles.btnContainer, { marginTop: 15 }]}>
            <Button title="Cancelar ❌" onPress={() => setMazoAEliminar(null)} color="#86BCBD" />
          </View>
        </View>
      </View>
    );
  }

  if (!mazoActivo) {
    return (
      <View style={styles.container}>
        <View style={styles.formCard}>
          <Text style={styles.header}>✨ Crear Nuevo Mazo ʕ•ᴥ•ʔ</Text>
          <TextInput style={styles.input} placeholder="Nombre de la materia..." value={nombreMazo} onChangeText={setNombreMazo} placeholderTextColor="#888" />
          <View style={styles.btnContainer}>
            <Button title="Guardar Mazo 💾" onPress={agregarMazo} color="#86BCBD" />
          </View>
        </View>

        <Text style={styles.headerTitle}>Mis Materias (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧</Text>
        <FlatList
          data={mazos}
          keyExtractor={item => item.id}
          renderItem={({ item }) => (
            <TouchableOpacity style={styles.card} onPress={() => setMazoActivo(item)}>
              <View style={styles.cardContent}>
                <View style={{ flex: 1 }}>
                  <Text style={styles.title}>📘 {item.nombre}</Text>
                  <Text style={styles.subtitle}>🗂️ {item.tarjetas.length} tarjetas</Text>
                </View>
                
                {/* Botón de basura añadido aquí */}
                <TouchableOpacity onPress={() => setMazoAEliminar(item)} style={styles.deleteBtn}>
                  <Text style={styles.deleteIcon}>🗑️</Text>
                </TouchableOpacity>
              </View>
            </TouchableOpacity>
          )}
        />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={{ marginBottom: 15 }}>
        <Button title="⬅️ Volver a mis mazos" onPress={() => setMazoActivo(null)} color="#BA5A5A" />
      </View>
      
      <View style={styles.formCard}>
        <Text style={styles.header}>Materia: {mazoActivo.nombre} ✏️</Text>
        <TextInput style={styles.input} placeholder="Escribe la pregunta..." value={pregunta} onChangeText={setPregunta} />
        <TextInput style={styles.input} placeholder="Escribe la respuesta..." value={respuesta} onChangeText={setRespuesta} />
        <View style={styles.btnContainer}>
          <Button title="Agregar Tarjeta ➕" onPress={agregarTarjeta} color="#A4CE8B" />
        </View>
      </View>

      <Text style={styles.headerTitle}>Tarjetas Guardadas (•̀ᴗ•́)و</Text>
      <FlatList
        data={mazoActivo.tarjetas}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <TouchableOpacity 
            style={styles.card} 
            onPress={() => { setTarjetaSeleccionada({ respuesta: item.respuesta }); setModalVisible(true); }}
          >
            <Text style={styles.title}>❓ {item.pregunta}</Text>
            <Text style={styles.subtitle}>Toca para ver la respuesta 👀</Text>
          </TouchableOpacity>
        )}
      />

      <CustomModal visible={modalVisible} onClose={() => setModalVisible(false)} contenido={tarjetaSeleccionada} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F7E49B', padding: 20 },
  // Nuevos estilos para la pantalla de confirmación (basados en EstudioScreen)
  containerCenter: { flex: 1, backgroundColor: '#F7E49B', padding: 20, justifyContent: 'center', alignItems: 'center' },
  emojiTriste: { fontSize: 50, marginBottom: 15, color: '#BA5A5A' },
  botonesFinales: { width: '85%', marginTop: 30 },
  
  formCard: { backgroundColor: 'white', padding: 20, borderRadius: 15, elevation: 5, shadowColor: '#000', shadowOffset: { width: 0, height: 2 }, shadowOpacity: 0.1, marginBottom: 20 },
  header: { fontSize: 20, fontWeight: "bold", marginBottom: 15, color: "#86BCBD", textAlign: 'center' },
  headerTitle: { fontSize: 22, fontWeight: "bold", marginVertical: 10, color: "#BA5A5A", textAlign: 'center' },
  input: { borderWidth: 2, borderColor: '#F7E49B', backgroundColor: '#fff', borderRadius: 10, padding: 12, marginBottom: 15, fontSize: 16 },
  btnContainer: { borderRadius: 10, overflow: 'hidden' },
  card: { backgroundColor: "white", padding: 20, marginVertical: 8, borderRadius: 15, borderLeftWidth: 6, borderLeftColor: '#86BCBD', elevation: 3 },
  
  // Nuevos estilos para organizar el texto y el botón del basurero
  cardContent: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  deleteBtn: { padding: 10, backgroundColor: '#FFF0F0', borderRadius: 50, borderWidth: 2, borderColor: '#BA5A5A' },
  deleteIcon: { fontSize: 22 },
  
  title: { fontSize: 18, fontWeight: "bold", color: "#333" },
  subtitle: { fontSize: 14, color: "gray", marginTop: 8 }
});