import { useState, useEffect, useRef } from 'react';
import { View, Text, StyleSheet, Animated } from 'react-native';
import { Gyroscope } from 'expo-sensors';

export default function GyroscopeSensor() {
    const [datos, setDatos] = useState({ x: 0, y: 0, z: 0 });
    const [mostrarBibble, setMostrarBibble] = useState(false);
    
    // Referencias para temporizador y animaciones
    const timerRef = useRef(null);
    
    // Valores animados para Bibble
    const opacityAnim = useRef(new Animated.Value(0)).current; 
    const scaleAnim = useRef(new Animated.Value(0.2)).current;
    const rotateAnim = useRef(new Animated.Value(0)).current; // Para que aparezca dando vueltas

    useEffect(() => {
        // 1.- Suscribirnos al sensor de Giroscopio
        const suscripcion = Gyroscope.addListener(mediciones => {
            setDatos(mediciones);

            const { x, y, z } = mediciones;
            
            // Calculamos la velocidad de rotación combinada
            const velocidadRotacion = Math.sqrt(x * x + y * y + z * z);

            // Si la velocidad supera los 2.5 radianes/s, consideramos que se está girando rápido
            if (velocidadRotacion > 2.5) {
                if (!mostrarBibble) {
                    setMostrarBibble(true);
                    
                    // Reiniciar el valor de rotación antes de empezar
                    rotateAnim.setValue(0);

                    // Animación mágica de Barbie (Aparece, Crece y Gira)
                    Animated.parallel([
                        Animated.timing(opacityAnim, {
                            toValue: 1, 
                            duration: 400,
                            useNativeDriver: true,
                        }),
                        Animated.spring(scaleAnim, {
                            toValue: 1, 
                            friction: 4, 
                            tension: 50,
                            useNativeDriver: true,
                        }),
                        Animated.timing(rotateAnim, {
                            toValue: 1,
                            duration: 600, // Gira rápido al aparecer
                            useNativeDriver: true,
                        })
                    ]).start();
                }

                // Si seguimos girando, reiniciamos el temporizador
                if (timerRef.current) clearTimeout(timerRef.current);
                
                timerRef.current = setTimeout(() => {
                    // Animación de salida (Se desvanece y se hace chiquito)
                    Animated.parallel([
                        Animated.timing(opacityAnim, {
                            toValue: 0,
                            duration: 400,
                            useNativeDriver: true,
                        }),
                        Animated.timing(scaleAnim, {
                            toValue: 0.2,
                            duration: 400,
                            useNativeDriver: true,
                        })
                    ]).start(() => {
                        setMostrarBibble(false);
                    });
                }, 2500); // Se queda 2.5 segundos en pantalla
            }
        });

        // 2.- Definir el intervalo de mediciones
        Gyroscope.setUpdateInterval(100); 

        // 3.- Limpieza al desmontar
        return () => {
            suscripcion.remove();
            if (timerRef.current) clearTimeout(timerRef.current);
        };
    }, [mostrarBibble]);

    // Interpolación para convertir el valor (0 a 1) en grados (0deg a 360deg)
    const girarInterpolado = rotateAnim.interpolate({
        inputRange: [0, 1],
        outputRange: ['0deg', '360deg']
    });

    return (
        <View style={styles.container}>
            <Text style={styles.title}>
                {mostrarBibble ? "¡Aaaah, me mareo! ✨" : "Gira el celular"}
            </Text>

            {/* Contenedor estático para que las tarjetas no salten */}
            <View style={styles.imageContainer}>
                {mostrarBibble && (
                    <Animated.Image 
                        source={{ uri: 'https://i.redd.it/some-of-my-favorite-bibble-memes-and-fan-edits-v0-74bb2z6t0vug1.jpg?width=1206&format=pjpg&auto=webp&s=79277860cc33b3804a7edfa639cc0c97587d5b94' }} // Imagen de Bibble
                        style={[
                            styles.bibbleImage,
                            { 
                                opacity: opacityAnim, 
                                transform: [
                                    { scale: scaleAnim },
                                    { rotate: girarInterpolado } // Aplicamos el giro mágico
                                ] 
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
        backgroundColor: '#fce4ec', // Rosa pastel al estilo Barbie
    },
    title: {
        fontSize: 26,
        fontWeight: 'bold',
        textAlign: 'center',
        marginBottom: 20,
        color: '#e91e63', // Rosa fuerte
    },
    imageContainer: {
        height: 180, 
        justifyContent: 'center',
        alignItems: 'center',
    },
    bibbleImage: {
        width: 160,
        height: 160,
        borderRadius: 80, // Circular
        borderWidth: 3,
        borderColor: '#f48fb1', // Borde rosadito
    },
    card: {
        backgroundColor: '#ffffff', // Tarjetas blancas para que destaquen
        padding: 20,
        marginBottom: 15,
        borderRadius: 15,
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '85%',
        shadowColor: '#e91e63', // Sombra rosa
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
        elevation: 3, // Sombra para Android
    },
    axis: {
        fontSize: 22,
        fontWeight: "bold",
        color: "#c2185b" // Rosa oscuro para las letras
    },
    value: {
        fontSize: 22,
        fontWeight: "bold",
        color: "#880e4f" // Rosa muy oscuro/vino para los números
    },
});