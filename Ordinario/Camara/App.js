import { CameraView, useCameraPermissions } from 'expo-camera';
import { useState, useRef } from 'react';
import { Button, StyleSheet, Text, View, Image } from 'react-native';

export default function App() {
  const [permiso, solicitarPermiso] = useCameraPermissions();
  const [fotoUri, setFotoUri] = useState(null);
  const cameraRef = useRef(null);

  // Mientras carga el estado del permiso
  if (!permiso) {
    return <View />;
  }

  // Si el permiso no ha sido otorgado
  if (!permiso.granted) {
    return (
      <View style={styles.container}>
        <Text style={styles.texto}>Necesitamos tu permiso para acceder a la cámara</Text>
        <Button onPress={solicitarPermiso} title="Otorgar permiso" />
      </View>
    );
  }

  // Función para capturar la fotografía
  const capturarFoto = async () => {
    if (cameraRef.current) {
      const foto = await cameraRef.current.takePictureAsync();
      setFotoUri(foto.uri);
    }
  };

  return (
    <View style={styles.container}>
      {fotoUri ? (
        <View style={styles.container}>
          <Image source={{ uri: fotoUri }} style={styles.camara} />
          <View style={styles.contenedorBoton}>
            <Button title="Tomar otra foto" onPress={() => setFotoUri(null)} />
          </View>
        </View>
      ) : (
        <CameraView style={styles.camara} ref={cameraRef} facing="back">
          <View style={styles.contenedorBoton}>
            <Button title="Capturar Fotografía" onPress={capturarFoto} />
          </View>
        </CameraView>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#000',
  },
  camara: {
    flex: 1,
    width: '100%',
  },
  contenedorBoton: {
    position: 'absolute',
    bottom: 40,
    alignSelf: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.8)',
    borderRadius: 10,
    padding: 5,
  },
  texto: {
    textAlign: 'center',
    marginBottom: 15,
    color: 'white',
  },
});