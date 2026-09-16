import {useState, useEffect} from 'react';
import {View, Text, StyleSheet} from 'react-native';
import {Magnetometer} from 'expo-sensors';

export default function MagnetometroSensor() {
    const [datos, setDatos] = useState({ 
        x: 0, 
        y: 0, 
        z: 0 
    });

    const grados = (Math.atan2(datos.y, datos.x) * 180) / Math.PI;
    const rumbo = (grados + 360) % 360;
    const puntosCardinales = ['N', 'NE', 'E', 'SE', 'S', 'SO', 'O', 'NO'];
    const puntoCardinal = puntosCardinales[Math.round(rumbo / 45) % 8];

    useEffect(() => {
        //1.- Suscribirnos al sensor
        const suscripcion = Magnetometer.addListener(mediciones => {
            setDatos(mediciones);
        });


        //2.- Definir el intervalo de mediciones
        Magnetometer.setUpdateInterval(100); 

        //3.- Definir la des-suscripción 
        return () => {
            suscripcion.remove();
        }
    }, []);

    return (
        <View style={styles.container}>
            <Text style={styles.emoji}>♡</Text>
            <Text style={styles.title}>Mi brújula</Text>
            <Text style={styles.subtitle}>encuentra tu dirección ✨</Text>

            <View style={styles.compass}>
                <Text style={[styles.direction, styles.north]}>N</Text>
                <Text style={[styles.direction, styles.east]}>E</Text>
                <Text style={[styles.direction, styles.south]}>S</Text>
                <Text style={[styles.direction, styles.west]}>O</Text>
                <View style={[styles.needleContainer, { transform: [{ rotate: `${rumbo}deg` }] }]}>
                    <View style={styles.needle} />
                </View>
                <View style={styles.centerDot} />
            </View>

            <View style={styles.result}>
                <Text style={styles.resultDirection}>{puntoCardinal}</Text>
                <Text style={styles.degrees}>{Math.round(rumbo)}°</Text>
            </View>
            <Text style={styles.hint}>♡ gira tu dispositivo suavemente ♡</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 24,
        backgroundColor: '#fff0f6',
    },
    emoji: { fontSize: 38, color: '#e88aaa', marginBottom: 2 },
    title: {
        fontSize: 31,
        fontWeight: 'bold',
        textAlign: 'center',
        color: '#c94f7c',
    },
    subtitle: { fontSize: 15, color: '#d784a2', marginBottom: 28 },
    compass: {
        width: 260,
        height: 260,
        borderRadius: 130,
        backgroundColor: '#ffd9e7',
        borderWidth: 12,
        borderColor: '#f5a9c4',
        shadowColor: '#d77d9d',
        shadowOpacity: 0.25,
        shadowRadius: 10,
        elevation: 6,
        position: 'relative',
    },
    direction: {
        position: 'absolute',
        fontSize: 24,
        fontWeight: "bold",
        color: '#bd527b',
        zIndex: 2,
    },
    north: { top: 10, alignSelf: 'center' },
    east: { right: 14, top: 108 },
    south: { bottom: 10, alignSelf: 'center' },
    west: { left: 14, top: 108 },
    needleContainer: {
        position: 'absolute',
        width: 36,
        height: 172,
        top: 44,
        left: 112,
        alignItems: 'center',
    },
    needle: {
        width: 0,
        height: 0,
        borderLeftWidth: 18,
        borderRightWidth: 18,
        borderBottomWidth: 86,
        borderLeftColor: 'transparent',
        borderRightColor: 'transparent',
        borderBottomColor: '#e66f99',
    },
    centerDot: { position: 'absolute', width: 22, height: 22, borderRadius: 11, backgroundColor: '#fff', top: 108, left: 108, borderWidth: 6, borderColor: '#e66f99' },
    result: { flexDirection: 'row', alignItems: 'baseline', marginTop: 24, gap: 10 },
    resultDirection: { fontSize: 30, fontWeight: 'bold', color: '#c94f7c' },
    degrees: { fontSize: 21, color: '#d784a2' },
    hint: { marginTop: 10, color: '#d784a2', fontSize: 13 },
});