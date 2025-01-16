
## GenericIOTInfrastructure

### **Project Overview**

**Generic IOTs Infrastructure** is a versatile project designed to provide infrastructure support for all types of IoT devices, regardless of the company or product. This infrastructure facilitates seamless device communication, data management, and runtime extensibility, ensuring scalability and adaptability for IoT ecosystems.

---

### **Backend Architecture**

The **Generic IOTs Infrastructure** is designed to be a modular and scalable architecture, as shown below:

#### **Gateway Server**
The server is divided into two main components:

1. **Connection Service**
    - Manages all incoming requests via **UDP**, **TCP**, or **HTTP**.
    - Routes these requests to the **Request Processing Service (RPS)** for handling.

2. **Request Processing Service (RPS)**
    - Processes incoming requests with the following features:
        - **Thread Pool**: Handles multiple requests concurrently using tasks.
        - **Request Parser**: Extracts key data from the request for processing.
        - **Command Factory**: Dynamically generates the appropriate **Command** object based on the parsed data.
        - **Command Execution**: Executes the logic defined in the created command.

    - **Plug-and-Play Service (Mediator)**
        - The new functionality available only for **TCP** and **UDP** requests.
        - Dynamically extends server functionality without requiring a restart.
        - Components:
            - **Directory Watcher (Observer)**: Monitors a directory for new `.jar` files containing command recipes.
            - **JAR Loader**: Loads new `.jar` files into the server to enable new functionality.

---


### **Databases**

1. **MongoDB**
    - Stores data about IoT devices
    - connected to the gateway server.

2. **SQL**
    - Stores administrative data of registered companies and products.
    - Connected to the webserver (Apache Tomcat)
---

### **Frontend**
The frontend is built using HTML, CSS, and JavaScript.
- It is hosted on an **Apache Tomcat** server.
- Custom **Servlets** are used to handle user requests and interact with the backend services.
---

### **Design Patterns Used**

1. **Command Pattern**
    - Encapsulates a request as an object, allowing dynamic command creation at runtime.
    - Ensures modularity and separation of concerns in request handling.

2. **Factory Pattern**
    - Responsible for creating commands in run time based on parsed requests.
    - Simplifies object creation and enhances flexibility.

3. **Thread Pool**
    - Manages multiple concurrent threads efficiently.
      - Reuse existed threads 

4. **Composite**
    - Implements a “has-a” relationship between objects.
    - Promotes modular design and code reusability.

5. **Observer Pattern**
    - Implements real-time updates for the **Plug-and-Play Service** to monitor new command `.jar` files.

6. **Singleton Pattern**
   - Ensure only single instance of the class created
   - Provide global point of access to the instance

7. **Mediator Pattern**
    - Used in the **Plug-and-Play Service** to coordinate the communication between the **Directory Watcher** and the **JAR Loader**, and between the **JAR Loader** to **RPS**, reducing direct dependencies and improving modularity.

