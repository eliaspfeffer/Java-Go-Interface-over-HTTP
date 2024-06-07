package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
)

// Beispiel-Methode, die ausgeführt werden soll
func exampleMethod(params map[string]interface{}) string {
	param := params["variable"].([]interface{})
	return fmt.Sprintf("Hello, %s!", strings.Join(convertToStringSlice(param), ", "))
}

// Another example method
func anotherExampleMethod(params map[string]interface{}) string {
	param := params["variable"].([]interface{})
	return fmt.Sprintf("Goodbye, %s!", strings.Join(convertToStringSlice(param), ", "))
}

// Helper function to convert interface{} slice to string slice
func convertToStringSlice(slice []interface{}) []string {
	strSlice := make([]string, len(slice))
	for i, v := range slice {
		strSlice[i] = fmt.Sprintf("%v", v)
	}
	return strSlice
}

// Handler-Funktion für die HTTP-Anfrage
func handler(w http.ResponseWriter, r *http.Request) {
	var params map[string]interface{}

	// JSON-Parameter auslesen
	decoder := json.NewDecoder(r.Body)
	if err := decoder.Decode(&params); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Methodenname holen
	method, ok := params["method"].(string)
	if !ok {
		http.Error(w, "Missing 'method' parameter", http.StatusBadRequest)
		return
	}

	// Ergebnis initialisieren
	var result string

	// Entscheiden, welche Methode aufgerufen werden soll
	switch method {
	case "exampleMethod":
		result = exampleMethod(params)
	case "anotherExampleMethod":
		result = anotherExampleMethod(params)
	default:
		http.Error(w, "Invalid method", http.StatusBadRequest)
		return
	}

	// Ergebnis als JSON zurückgeben
	response := map[string]string{"result of go": result}
	json.NewEncoder(w).Encode(response)
}

func main() {
	http.HandleFunc("/execute", handler)
	fmt.Println("Starting server on port 8080...")
	http.ListenAndServe(":8080", nil)
}
