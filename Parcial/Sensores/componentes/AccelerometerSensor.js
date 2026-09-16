import { useState, useEffect, useRef } from 'react';
import { View, Text, StyleSheet, Image, Animated } from 'react-native';
import { Accelerometer } from 'expo-sensors';

export default function GyroscopeSensor() {
    const [datos, setDatos] = useState({ x: 0, y: 0, z: 0 });
    const [mostrarPou, setMostrarPou] = useState(false);
    
    // Referencias para la animación y el temporizador
    const timerRef = useRef(null);
    
    // Valores animados: 
    // opacityAnim para que aparezca suavemente (Fade)
    // scaleAnim para que haga un efecto de rebote/crecimiento
    const opacityAnim = useRef(new Animated.Value(0)).current; 
    const scaleAnim = useRef(new Animated.Value(0.5)).current;

    useEffect(() => {
        // 1.- Suscribirnos al sensor
        const suscripcion = Accelerometer.addListener(mediciones => {
            setDatos(mediciones);

            const { x, y, z } = mediciones;
            const fuerza = Math.sqrt(x * x + y * y + z * z);

            if (fuerza > 1.7) {
                if (!mostrarPou) {
                    setMostrarPou(true);
                    
                    // Iniciar la animación al sacudir
                    Animated.parallel([
                        Animated.timing(opacityAnim, {
                            toValue: 1, // Llega a opacidad 1 (completamente visible)
                            duration: 300, // En 300 milisegundos
                            useNativeDriver: true,
                        }),
                        Animated.spring(scaleAnim, {
                            toValue: 1, // Tamaño normal
                            friction: 4, // Efecto de rebote
                            tension: 40,
                            useNativeDriver: true,
                        })
                    ]).start();
                }

                // Reiniciar el temporizador si se sigue sacudiendo
                if (timerRef.current) clearTimeout(timerRef.current);
                
                timerRef.current = setTimeout(() => {
                    // Animación de salida (Fade out)
                    Animated.timing(opacityAnim, {
                        toValue: 0,
                        duration: 300,
                        useNativeDriver: true,
                    }).start(() => {
                        // Ocultarlo completamente después de la animación de salida
                        setMostrarPou(false);
                        scaleAnim.setValue(0.5); // Reiniciar escala para la próxima vez
                    });
                }, 2000);
            }
        });

        // 2.- Definir el intervalo de mediciones
        Accelerometer.setUpdateInterval(100); 

        // 3.- Limpieza
        return () => {
            suscripcion.remove();
            if (timerRef.current) clearTimeout(timerRef.current);
        };
    }, [mostrarPou]); // Agregamos mostrarPou a las dependencias

    return (
        <View style={styles.container}>
            <Text style={styles.title}>
                {mostrarPou ? "¡Deja de sacudirme! 🖤" : "Sacude el celular"}
            </Text>

            {/* Contenedor que ocupa un espacio fijo para que las tarjetas no salten */}
            <View style={styles.imageContainer}>
                {mostrarPou && (
                    <Animated.Image 
                        source={{ uri: 'https://i.pinimg.com/474x/4b/8d/1c/4b8d1cc1ac8d0efaaa492f1a45bf63ef.jpg' }} 
                        style={[
                            styles.pouImage,
                            { 
                                opacity: opacityAnim, // Aplicamos la opacidad animada
                                transform: [{ scale: scaleAnim }] // Aplicamos la escala animada
                            }
                        ]}
                    />
                )}
            </View>

            <View style={styles.card}>
                <Text style={styles.axis}>X:</Text>
                <Text style={styles.value}>{datos.x.toFixed(2)}</Text>
            </View>
            <View style={styles.card}>
                <Text style={styles.axis}>Y:</Text>
                <Text style={styles.value}>{datos.y.toFixed(2)}</Text>
            </View>
            <View style={styles.card}>
                <Text style={styles.axis}>Z:</Text>
                <Text style={styles.value}>{datos.z.toFixed(2)}</Text>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 25,
        backgroundColor: '#121212', 
    },
    title: {
        fontSize: 26,
        fontWeight: 'bold',
        textAlign: 'center',
        marginBottom: 20,
        color: '#dc1212', 
    },
    // Contenedor para evitar que las tarjetas se muevan cuando Pou aparece/desaparece
    imageContainer: {
        height: 170, // Espacio suficiente para la imagen + margen
        justifyContent: 'center',
        alignItems: 'center',
    },
    pouImage: {
        width: 150,
        height: 150,
        borderRadius: 75,
        borderWidth: 2,
        borderColor: '#bb86fc', 
    },
    card: {
        backgroundColor: '#54a6e9', 
        padding: 20,
        marginBottom: 15,
        borderRadius: 10,
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '80%',
        borderWidth: 1,
        borderColor: '#333',
    },
    axis: {
        fontSize: 22,
        fontWeight: "bold",
        color: "#121212" // Cambié esto a oscuro para que contraste mejor con el azul (#54a6e9)
    },
    value: {
        fontSize: 22,
        fontWeight: "bold",
        color: "#ffffff" 
    },
});