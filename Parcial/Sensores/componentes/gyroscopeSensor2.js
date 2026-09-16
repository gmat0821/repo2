import { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Dimensions, Platform, StatusBar } from 'react-native';
import { Gyroscope } from 'expo-sensors';

// Obtener las dimensiones de la pantalla completa
const { width, height } = Dimensions.get('window');

// Evitar que la pelota se esconda detrás de la barra de estado en Android/Notch en iOS
const topSafeArea = Platform.OS === 'ios' ? 50 : StatusBar.currentHeight || 0;
const ballSize = 40;

export default function GyroscopeSensorCompleto() {
    const [datos, setDatos] = useState({ 
        x: 0, 
        y: 0, 
        z: 0 
    });
    
    // Estado para la posición de la bolita. Empezamos en el centro de la pantalla.
    const [ballPos, setBallPos] = useState({ 
        x: (width - ballSize) / 2, 
        y: (height - ballSize) / 2 
    });

    useEffect(() => {
        // 1.- Configurar el intervalo de mediciones (más rápido para mayor fluidez)
        Gyroscope.setUpdateInterval(16); // ~60 actualizaciones por segundo

        // 2.- Suscribirnos al sensor
        const suscripcion = Gyroscope.addListener(mediciones => {
            setDatos(mediciones);

            // 3.- Calcular la nueva posición basada en VELOCIDAD (acumulativa)
            
            // Factor de sensibilidad (cuánto afecta la rotación a la velocidad)
            const sensibilidad = 5; 
            // Factor de fricción (0 a 1). Ayuda a detener la pelota si dejas de mover el teléfono.
            const friccion = 0.98; 

            setBallPos((prevPos) => {
                // Mapeo de ejes para modo retrato (Portrait):
                // Girar hacia los lados (y) mueve en eje X de pantalla.
                // Inclinar adelante/atrás (x) mueve en eje Y de pantalla.
                
                // Calculamos nueva posición sumando la velocidad actual a la posición anterior
                let nextX = prevPos.x + (mediciones.y * sensibilidad);
                let nextY = prevPos.y + (mediciones.x * sensibilidad);

                // Aplicar fricción simple a la velocidad implícita
                // (Opcional, hace el movimiento más controlable)
                // nextX = prevPos.x + ((nextX - prevPos.x) * friccion);
                // nextY = prevPos.y + ((nextY - prevPos.y) * friccion);

                // 4.- Clamping Extremo (Límites de toda la pantalla)
                
                // Límites Horizontales
                const limiteXDerecho = width - ballSize;
                if (nextX < 0) nextX = 0;
                if (nextX > limiteXDerecho) nextX = limiteXDerecho;

                // Límites Verticales (tomando en cuenta área segura superior)
                const limiteYInferior = height - ballSize;
                if (nextY < topSafeArea) nextY = topSafeArea;
                if (nextY > limiteYInferior) nextY = limiteYInferior;

                return { x: nextX, y: nextY };
            });
        });

        // 5.- Definir la des-suscripción
        return () => {
            suscripcion.remove();
        }
    }, []);

    return (
        <View style={styles.container}>
            {/* Contenido de fondo (UI estática) */}
            <View style={styles.uiOverlay}>
                <Text style={styles.title}>Giroscopio Total</Text>
                <Text style={styles.instrucciones}>Inclina el teléfono para mover la pelota</Text>
                
                <View style={styles.dataContainer}>
                    <View style={styles.card}>
                        <Text style={styles.axis}>X (w)</Text>
                        <Text style={styles.value}>{datos.x.toFixed(2)}</Text>
                    </View>
                    <View style={styles.card}>
                        <Text style={styles.axis}>Y (w)</Text>
                        <Text style={styles.value}>{datos.y.toFixed(2)}</Text>
                    </View>
                </View>
            </View>

            {/* La pelota flotando por encima de todo */}
            <View style={[
                styles.ball, 
                { 
                    transform: [
                        { translateX: ballPos.x }, 
                        { translateY: ballPos.y }
                    ] 
                }
            ]} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#2c3e50', // Color de fondo oscuro y plano
    },
    // Estilos para la interfaz que queda de fondo
    uiOverlay: {
        flex: 1,
        justifyContent: 'center', // Centrar la UI estática verticalmente
        alignItems: 'center',
        padding: 25,
        zIndex: 1, // Por debajo de la pelota
    },
    title: {
        fontSize: 32,
        fontWeight: 'bold',
        textAlign: 'center',
        marginBottom: 10,
        color: '#ecf0f1',
    },
    instrucciones: {
        fontSize: 16,
        color: '#bdc3c7',
        marginBottom: 30,
    },
    // Estilos de la pelota (ahora absoluta)
    ball: {
        width: ballSize,
        height: ballSize,
        borderRadius: ballSize / 2,
        backgroundColor: '#e74c3c', // Rojo vibrante
        position: 'absolute', // Importante para mover por toda la pantalla
        top: 0,
        left: 0,
        zIndex: 10, // Por encima de toda la UI
        // Sombra para efecto 3D
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.30,
        shadowRadius: 4.65,
        elevation: 8,
    },
    dataContainer: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        width: '100%',
        marginTop: 20,
    },
    card: {
        backgroundColor: 'rgba(255,255,255,0.1)', // Transparente
        padding: 15,
        borderRadius: 10,
        alignItems: 'center',
        width: '45%',
    },
    axis: {
        fontSize: 18,
        fontWeight: "bold",
        color: "#2ecc71", // Verde
        marginBottom: 5,
    },
    value: {
        fontSize: 24,
        fontWeight: "bold",
        color: "#fff",
    },
});