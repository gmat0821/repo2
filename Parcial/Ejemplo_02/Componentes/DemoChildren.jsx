import {view} from "reac-native";

export default function DemoChildren( {children, titulo}) {
    return (
        <View>
            <Text>{titulo}</Text>
            <Text> Muestra children</Text> {children}
            
        </View>
    );
}