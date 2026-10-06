import React, { useState } from 'react';
import { View, Text, Button, FlatList, TouchableOpacity, StyleSheet } from 'react-native';

export default function EstudioScreen({ mazos }) {
  const [mazoActivo, setMazoActivo] = useState(null);
  const [tarjetasRestantes, setTarjetasRestantes] = useState([]);
  const [tarjetasEquivocadas, setTarjetasEquivocadas] = useState([]);
  const [preguntaActual, setPreguntaActual] = useState(null);
  const [mostrarRespuesta, setMostrarRespuesta] = useState(false);
  const [puntaje, setPuntaje] = useState(0);
  const [juegoTerminado, setJuegoTerminado] = useState(false);
  const [totalTarjetasJuego, setTotalTarjetasJuego] = useState(0);

  const iniciarJuego = (tarjetasParaJugar) => {
    setTarjetasRestantes(tarjetasParaJugar);
    setTotalTarjetasJuego(tarjetasParaJugar.length);
    setTarjetasEquivocadas([]);
    setPuntaje(0);
    setJuegoTerminado(false);
    sacarPreguntaAlAzar(tarjetasParaJugar);
  };

  const seleccionarMazo = (mazo) => {
    setMazoActivo(mazo);
    iniciarJuego(mazo.tarjetas);
  };

  const sacarPreguntaAlAzar = (tarjetasDisponibles) => {
    if (tarjetasDisponibles.length > 0) {
      const indiceAleatorio = Math.floor(Math.random() * tarjetasDisponibles.length);
      setPreguntaActual(tarjetasDisponibles[indiceAleatorio]);
      setMostrarRespuesta(false); 
    } else {
      setJuegoTerminado(true);
    }
  };

  const calificarRespuesta = (esCorrecta) => {
    if (esCorrecta) {
      setPuntaje(puntaje + 1);
    } else {
      setTarjetasEquivocadas([...tarjetasEquivocadas, preguntaActual]);
    }
    const nuevasRestantes = tarjetasRestantes.filter((t) => t.id !== preguntaActual.id);
    setTarjetasRestantes(nuevasRestantes);
    sacarPreguntaAlAzar(nuevasRestantes);
  };

  if (!mazoActivo) {
    return (
      <View style={styles.container}>
        <Text style={styles.headerTitle}>🕹️ Elige un mazo para jugar ʕ•ᴥ•ʔ</Text>
        <FlatList
          data={mazos}
          keyExtractor={item => item.id}
          renderItem={({ item }) => (
            <TouchableOpacity 
               style={styles.cardMazo} 
               onPress={() => {
                 if(item.tarjetas.length > 0){ seleccionarMazo(item); } 
                 else { alert("Este mazo está vacío (╥﹏╥)"); }
               }}
            >
              <Text style={styles.title}>🎲 {item.nombre}</Text>
              <Text style={styles.subtitle}>▶️ {item.tarjetas.length} tarjetas disponibles</Text>
            </TouchableOpacity>
          )}
        />
      </View>
    );
  }

  if (juegoTerminado) {
    return (
      <View style={styles.containerCenter}>
        <Text style={styles.gameOverTitle}>(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧</Text>
        <Text style={styles.header}>¡Juego Terminado!</Text>
        <Text style={styles.scoreText}>
          Aciertos: {puntaje} / {totalTarjetasJuego} 🌟
        </Text>

        <View style={styles.botonesFinales}>
          <View style={styles.btnWrapper}>
            <Button title="Empezar de nuevo 🔄" onPress={() => iniciarJuego(mazoActivo.tarjetas)} color="#86BCBD" />
          </View>
          
          {tarjetasEquivocadas.length > 0 && (
            <View style={styles.btnWrapper}>
              <Button title="Repetir equivocadas (ง •̀_•́)ง" onPress={() => iniciarJuego(tarjetasEquivocadas)} color="#BA5A5A" />
            </View>
          )}

          <View style={styles.btnWrapper}>
            <Button title="Salir al menú 🚪" onPress={() => setMazoActivo(null)} color="#555" />
          </View>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.topRow}>
        <Button title="⬅️ Salir" onPress={() => setMazoActivo(null)} color="#BA5A5A" />
        <Text style={styles.headerScore}>Restantes: {tarjetasRestantes.length} 🃏</Text>
      </View>

      {preguntaActual && (
        <View style={styles.cardBox}>
          <Text style={styles.preguntaTexto}>🤔 {preguntaActual.pregunta}</Text>
          
          {!mostrarRespuesta ? (
            <View style={styles.btnWrapper}>
              <Button title="Revelar Respuesta 💡" onPress={() => setMostrarRespuesta(true)} color="#86BCBD" />
            </View>
          ) : (
            <View style={styles.respuestaContainer}>
              <Text style={styles.respuestaTexto}>✨ {preguntaActual.respuesta} ✨</Text>
              <Text style={styles.instruccion}>¿Acertaste? ＼(￣▽￣)／</Text>
              
              <View style={styles.emojiBotonesRow}>
                <TouchableOpacity style={[styles.emojiBtn, {borderColor: '#BA5A5A'}]} onPress={() => calificarRespuesta(false)}>
                  <Text style={styles.emojiText}>❌</Text>
                </TouchableOpacity>

                <TouchableOpacity style={[styles.emojiBtn, {borderColor: '#A4CE8B'}]} onPress={() => calificarRespuesta(true)}>
                  <Text style={styles.emojiText}>✅</Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F7E49B', padding: 20 },
  containerCenter: { flex: 1, backgroundColor: '#F7E49B', padding: 20, justifyContent: 'center', alignItems: 'center' },
  topRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  
  headerTitle: { fontSize: 24, fontWeight: "bold", marginVertical: 15, color: "#BA5A5A", textAlign: 'center' },
  header: { fontSize: 28, fontWeight: "bold", color: "#86BCBD", textAlign: 'center', marginBottom: 10 },
  headerScore: { fontSize: 18, fontWeight: "bold", color: "#86BCBD" },
  gameOverTitle: { fontSize: 40, marginBottom: 10 },
  
  cardMazo: { backgroundColor: "white", padding: 20, marginVertical: 8, borderRadius: 15, borderLeftWidth: 6, borderLeftColor: '#A4CE8B', elevation: 3 },
  title: { fontSize: 18, fontWeight: "bold", color: "#333" },
  subtitle: { fontSize: 14, color: "gray", marginTop: 8 },
  
  cardBox: { backgroundColor: "white", padding: 30, borderRadius: 20, width: '100%', alignItems: 'center', elevation: 8, shadowColor: '#000', shadowOffset: { width: 0, height: 4 }, shadowOpacity: 0.2, marginTop: 20, borderWidth: 2, borderColor: '#86BCBD' },
  preguntaTexto: { fontSize: 24, fontWeight: "bold", textAlign: 'center', marginBottom: 30, color: "#333" },
  
  respuestaContainer: { width: '100%', alignItems: 'center', marginTop: 10 },
  respuestaTexto: { fontSize: 22, color: "#BA5A5A", textAlign: 'center', marginBottom: 25, fontWeight: 'bold' },
  instruccion: { fontSize: 18, color: "#555", marginBottom: 20, fontStyle: 'italic' },
  
  emojiBotonesRow: { flexDirection: 'row', justifyContent: 'space-around', width: '100%', marginTop: 10 },
  emojiBtn: { padding: 15, backgroundColor: '#fff', borderRadius: 50, elevation: 4, borderWidth: 3 },
  emojiText: { fontSize: 40 },
  
  botonesFinales: { width: '85%', marginTop: 40 },
  btnWrapper: { marginBottom: 15, borderRadius: 10, overflow: 'hidden' },
  scoreText: { fontSize: 24, color: '#A4CE8B', fontWeight: 'bold', backgroundColor: 'white', padding: 15, borderRadius: 10, overflow: 'hidden', elevation: 2 }
});